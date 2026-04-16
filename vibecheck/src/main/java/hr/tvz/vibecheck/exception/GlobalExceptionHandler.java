package hr.tvz.vibecheck.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseService errorResponseService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateFollowRequestException.class)
    public ResponseEntity<?> handleDuplicateFollowRequestException(DuplicateFollowRequestException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateFollowException.class)
    public ResponseEntity<?> handleDuplicateFollowRequestException(DuplicateFollowException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
