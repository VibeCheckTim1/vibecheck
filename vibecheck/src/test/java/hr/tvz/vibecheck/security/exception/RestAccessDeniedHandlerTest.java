package hr.tvz.vibecheck.security.exception;

import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestAccessDeniedHandlerTest {

    @Mock
    private ErrorResponseService errorResponseService;

    private RestAccessDeniedHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RestAccessDeniedHandler(errorResponseService);
    }

    @Test
    void handle_shouldCallWriteErrorWithForbiddenKey() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        handler.handle(request, response, exception);

        verify(errorResponseService).writeError(
                request,
                response,
                exception,
                ErrorKey.FORBIDDEN
        );
    }

    @Test
    void handle_shouldDelegateToErrorResponseService() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException exception = new AccessDeniedException("You do not have permission");

        handler.handle(request, response, exception);

        verify(errorResponseService).writeError(any(), any(), any(), eq(ErrorKey.FORBIDDEN));
    }
}
