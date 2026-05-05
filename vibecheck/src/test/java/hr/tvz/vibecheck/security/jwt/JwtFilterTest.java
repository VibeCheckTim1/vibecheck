package hr.tvz.vibecheck.security.jwt;

import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(accessTokenService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/security/refresh-token",
            "/security/login",
            "/security/register",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/v3/api-docs/swagger-config"
    })
    void doFilter_whenPathShouldBeSkipped_shouldSkipJwtProcessing(String path)
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(accessTokenService);
    }

    @Test
    void doFilter_whenNoCookiePresent_shouldContinueWithoutAuth() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/resource");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(accessTokenService.extractTokenFromCookie(request)).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_whenValidToken_shouldSetAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/resource");
        request.setCookies(new Cookie("ACCESS", "valid-jwt-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        VibeCheckUserDetails userDetails = VibeCheckUserDetails.builder()
                .username("testuser")
                .password("pass")
                .roles(List.of("USER"))
                .build();

        when(accessTokenService.extractTokenFromCookie(request)).thenReturn("valid-jwt-token");
        when(accessTokenService.isValid("valid-jwt-token")).thenReturn(true);
        when(accessTokenService.extractUsername("valid-jwt-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("testuser");
    }

    @Test
    void doFilter_whenTokenIsInvalid_shouldReturnUnauthorized() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/resource");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(accessTokenService.extractTokenFromCookie(request)).thenReturn("invalid-token");
        when(accessTokenService.isValid("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("Invalid or expired token");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_whenTokenIsValidButNotMatching_shouldNotSetAuth() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/resource");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(accessTokenService.extractTokenFromCookie(request)).thenReturn("some-token");
        when(accessTokenService.isValid("some-token")).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
