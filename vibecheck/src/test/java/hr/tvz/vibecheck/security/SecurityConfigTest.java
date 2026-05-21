package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.security.exception.MDCfilter;
import hr.tvz.vibecheck.security.exception.RestAccessDeniedHandler;
import hr.tvz.vibecheck.security.exception.RestAuthenticationEntryPoint;
import hr.tvz.vibecheck.security.jwt.JwtFilter;
import hr.tvz.vibecheck.security.oauth.CustomOAuth2UserService;
import hr.tvz.vibecheck.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import hr.tvz.vibecheck.security.oauth.OAuth2AuthenticationFailureHandler;
import hr.tvz.vibecheck.security.oauth.OAuth2AuthenticationSuccessHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    private static final String FRONTEND_URL = "http://127.0.0.1:5173";

    @Mock private MDCfilter mdcFilter;
    @Mock private JwtFilter jwtFilter;
    @Mock private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Mock private RestAccessDeniedHandler restAccessDeniedHandler;
    @Mock private CustomOAuth2UserService customOAuth2UserService;
    @Mock private EndpointAuthorizationService endpointAuthorizationService;
    @Mock private OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    @Mock private OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
    @Mock private HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    private SecurityConfig securityConfig() {
        return new SecurityConfig(
                mdcFilter, jwtFilter, restAuthenticationEntryPoint, restAccessDeniedHandler,
                customOAuth2UserService, endpointAuthorizationService,
                oauth2AuthenticationSuccessHandler, oauth2AuthenticationFailureHandler,
                cookieAuthorizationRequestRepository
        );
    }

    @Test
    void corsConfigurationSource_shouldAllowFrontendOrigin() {
        SecurityConfig config = securityConfig();

        CorsConfigurationSource source = config.corsConfigurationSource(FRONTEND_URL);

        assertThat(source).isNotNull().isInstanceOf(UrlBasedCorsConfigurationSource.class);

        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;
        CorsConfiguration corsConfig = urlSource.getCorsConfigurations().get("/**");
        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).containsExactly(FRONTEND_URL);
    }

    @Test
    void corsConfigurationSource_shouldAllowAllHeaders() {
        SecurityConfig config = securityConfig();

        CorsConfigurationSource source = config.corsConfigurationSource(FRONTEND_URL);
        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;
        CorsConfiguration corsConfig = urlSource.getCorsConfigurations().get("/**");

        assertThat(corsConfig.getAllowedHeaders()).containsExactly("*");
    }

    @Test
    void corsConfigurationSource_shouldAllowCredentials() {
        SecurityConfig config = securityConfig();

        CorsConfigurationSource source = config.corsConfigurationSource(FRONTEND_URL);
        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;
        CorsConfiguration corsConfig = urlSource.getCorsConfigurations().get("/**");

        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }

    @Test
    void corsConfigurationSource_shouldAllowHttpMethods() {
        SecurityConfig config = securityConfig();

        CorsConfigurationSource source = config.corsConfigurationSource(FRONTEND_URL);
        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;
        CorsConfiguration corsConfig = urlSource.getCorsConfigurations().get("/**");

        List<String> methods = corsConfig.getAllowedMethods();
        assertThat(methods).containsExactlyInAnyOrder("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}
