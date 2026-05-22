package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.security.exception.MDCfilter;
import hr.tvz.vibecheck.security.exception.RestAccessDeniedHandler;
import hr.tvz.vibecheck.security.exception.RestAuthenticationEntryPoint;
import hr.tvz.vibecheck.security.jwt.JwtFilter;
import hr.tvz.vibecheck.security.oauth.OAuth2AuthenticationFailureHandler;
import hr.tvz.vibecheck.security.oauth.CustomOAuth2UserService;
import hr.tvz.vibecheck.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import hr.tvz.vibecheck.security.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MDCfilter mdcFilter;

    private final JwtFilter jwtFilter;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final RestAccessDeniedHandler restAccessDeniedHandler;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final EndpointAuthorizationService endpointAuthorizationService;

    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Bean
    @SuppressWarnings("java:S4502") // CSRF može biti ugašen
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .cors(_ -> {})
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .addFilterBefore(mdcFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, MDCfilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oauth2AuthenticationSuccessHandler)
                        .failureHandler(oauth2AuthenticationFailureHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        endpointAuthorizationService.getEndpointAccessRules().forEach(
                                rule -> {
                                    if (rule.roles().isEmpty()) {
                                        auth.requestMatchers(rule.method(), rule.path()).authenticated();
                                        return;
                                    }
                                    auth.requestMatchers(rule.method(), rule.path())
                                            .hasAnyRole(rule.roles().toArray(String[]::new));
                                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/security/register").permitAll()
                        .requestMatchers("/security/login").permitAll()
                        .requestMatchers("/security/refresh-token").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                        //.requestMatchers("/security/current-user").authenticated()
                        //.requestMatchers("/security/logout").authenticated()
                        //.requestMatchers("/user/**").authenticated()
                        //.requestMatchers("/search").authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(frontendBaseUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
