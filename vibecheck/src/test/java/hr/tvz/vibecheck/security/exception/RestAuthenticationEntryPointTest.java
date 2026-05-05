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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    private ErrorResponseService errorResponseService;

    private RestAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new RestAuthenticationEntryPoint(errorResponseService);
    }

    @Test
    void commence_shouldCallWriteErrorWithUnauthorizedKey() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = new BadCredentialsException("Bad credentials");

        entryPoint.commence(request, response, exception);

        verify(errorResponseService).writeError(
                request,
                response,
                exception,
                ErrorKey.UNAUTHORIZED
        );
    }

    @Test
    void commence_shouldDelegateToErrorResponseService() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = new BadCredentialsException("Unauthorized");

        entryPoint.commence(request, response, exception);

        verify(errorResponseService).writeError(any(), any(), any(), eq(ErrorKey.UNAUTHORIZED));
    }
}
