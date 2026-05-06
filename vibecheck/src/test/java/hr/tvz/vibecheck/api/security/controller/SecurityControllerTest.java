package hr.tvz.vibecheck.api.security.controller;

import hr.tvz.vibecheck.api.security.dto.TokenOutputDto;
import hr.tvz.vibecheck.api.security.projections.UserStateResponse;
import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.api.security.service.RefreshTokenService;
import hr.tvz.vibecheck.api.security.service.SecurityService;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import hr.tvz.vibecheck.security.SecurityConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SecurityController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class SecurityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean SecurityService securityService;
    @MockitoBean AccessTokenService accessTokenService;
    @MockitoBean RefreshTokenService refreshTokenService;
    @MockitoBean ErrorResponseService errorResponseService;
    @MockitoBean UserDetailsService userDetailsService;

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) {
            return http.csrf(AbstractHttpConfigurer::disable)
                       .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                       .build();
        }
    }

    @Test
    @DisplayName("POST /security/login - 200 s validnim kredencijalima")
    void login_withValidCredentials_returns200() throws Exception {
        when(securityService.loginWithCredentials(any(), any())).thenReturn(new TokenOutputDto("access", "refresh"));
        when(accessTokenService.generateTokenCookie(anyString())).thenReturn(new Cookie("ACCESS", "access"));
        when(refreshTokenService.generateTokenCookie(anyString())).thenReturn(new Cookie("REFRESH", "refresh"));
        when(securityService.getCurrentUser()).thenReturn(mock(UserStateResponse.class));

        mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "lsaric", "password": "password" }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /security/logout - 204 briše kolačiće")
    void logout_returns204() throws Exception {
        mockMvc.perform(post("/security/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /security/current-user - 401 kad nema korisnika u kontekstu")
    void currentUser_whenNoUser_returns401() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(null);

        mockMvc.perform(get("/security/current-user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /security/current-user - 200 kad korisnik postoji")
    void currentUser_whenUserExists_returns200() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(mock(UserStateResponse.class));

        mockMvc.perform(get("/security/current-user"))
                .andExpect(status().isOk());
    }
}