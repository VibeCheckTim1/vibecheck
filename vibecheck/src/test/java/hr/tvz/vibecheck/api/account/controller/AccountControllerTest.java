package hr.tvz.vibecheck.api.account.controller;

import hr.tvz.vibecheck.api.account.service.AccountService;
import hr.tvz.vibecheck.api.security.projections.UserStateResponse;
import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.api.security.service.SecurityService;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import hr.tvz.vibecheck.security.SecurityConfig;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean AccountService accountService;
    @MockitoBean SecurityService securityService;
    @MockitoBean AccessTokenService accessTokenService;
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

    private UsernamePasswordAuthenticationToken mockAuth() {
        var userDetails = VibeCheckUserDetails.builder()
                .id(1L)
                .username("kstjepanovic")
                .password("1234")
                .roles(List.of("USER"))
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("PATCH /account - 200 s validnim podacima")
    void updateAccount_withValidData_returns200() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(mock(UserStateResponse.class));

        mockMvc.perform(patch("/account")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "firstName": "Karlo", "lastName": "Stjepanović", "bio": "bio", "isPrivate": false }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /account - 401 kad getCurrentUser vrati null")
    void updateAccount_whenNoUserInContext_returns401() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(null);

        mockMvc.perform(patch("/account")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "firstName": "Karlo", "lastName": "Stjepanović" }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /account - 204 i kolačići obrisani")
    void deleteAccount_returns204AndClearsCookies() throws Exception {
        mockMvc.perform(delete("/account")
                        .with(authentication(mockAuth())))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("ACCESS", 0))
                .andExpect(cookie().maxAge("REFRESH", 0));
    }

    @Test
    @DisplayName("POST /account/avatar - 200 s validnom slikom")
    void uploadAvatar_withValidImage_returns200() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(mock(UserStateResponse.class));

        var file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "fake-image".getBytes());

        mockMvc.perform(multipart("/account/avatar")
                        .file(file)
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /account/avatar - 401 kad getCurrentUser vrati null")
    void uploadAvatar_whenNoUserInContext_returns401() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(null);

        var file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "fake-image".getBytes());

        mockMvc.perform(multipart("/account/avatar")
                        .file(file)
                        .with(authentication(mockAuth())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /account/password - 204 s validnim podacima")
    void changePassword_withValidData_returns204() throws Exception {
        mockMvc.perform(patch("/account/password")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "oldPassword": "stara123", "newPassword": "nova123" }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /account/emailVerificationCode - 200 s validnim emailom")
    void sendEmailVerificationCode_withValidEmail_returns200() throws Exception {
        mockMvc.perform(post("/account/emailVerificationCode")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "newEmail": "novi@email.hr" }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /account/confirmMailEdit - 200 s validnim kodom")
    void confirmMailEdit_withValidCode_returns200() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(mock(UserStateResponse.class));

        mockMvc.perform(patch("/account/confirmMailEdit")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "code": "123456" }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /account/confirmMailEdit - 401 kad getCurrentUser vrati null")
    void confirmMailEdit_whenNoUserInContext_returns401() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(null);

        mockMvc.perform(patch("/account/confirmMailEdit")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "code": "123456" }
                                """))
                .andExpect(status().isUnauthorized());
    }
}