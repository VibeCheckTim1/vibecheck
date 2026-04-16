package hr.tvz.vibecheck.exception.framework;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorResponseService {

    private final ErrorProperties errorProperties;

    private final ObjectMapper objectMapper;

    public ResponseEntity<ErrorResponse> buildError(Exception ex, HttpServletRequest request, ErrorKey errorKey) {
        return buildError(ex, request, errorProperties.get(errorKey));
    }

    public ResponseEntity<ErrorResponse> buildError(Exception ex, HttpServletRequest request, ErrorEntry errorEntry) {
        var sid = MDC.get("sid");
        if (sid == null || sid.isBlank()) {
            sid = "00000000000000000000000000000000";
            MDC.put("sid", sid);
        }

        var httpStatus = HttpStatus.valueOf(errorEntry.code());

        String message;
        if (ex.getMessage() != null) message = ex.getMessage();
        else if (errorEntry.message() != null) message = errorEntry.message();
        else message = httpStatus.getReasonPhrase();

        var errorResponse = new ErrorResponse(
                sid,
                request.getRequestURI(),
                httpStatus.value(),
                message,
                LocalDateTime.now()
        );

        log.info("Handling exception: {}", errorResponse);
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    public void writeError(HttpServletRequest request,
                           HttpServletResponse response,
                           Exception ex,
                           ErrorKey errorKey) throws IOException {
        var errorEntity = buildError(ex, request, errorKey);

        response.setStatus(errorEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (errorEntity.getBody() != null) {
            objectMapper.writeValue(response.getWriter(), errorEntity.getBody());
        }
    }
}
