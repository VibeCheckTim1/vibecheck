package hr.tvz.vibecheck.security.exception;

import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorResponseService errorResponseService;

    @Override
    public void handle(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException {
        errorResponseService.writeError(request, response, accessDeniedException, ErrorKey.FORBIDDEN);
    }
}
