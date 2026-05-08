package hr.tvz.vibecheck.api.user.controller;

import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.user.service.UserService;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ErrorResponseService errorResponseService;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserOutputDto sampleDto() {
        return new UserOutputDto(1L, "johndoe", "John", "Doe", "Bio text", "john@email.com", "avatar.png", false);
    }

    @Test
    void show_shouldReturn200WithUser_whenUserExists() throws Exception {
        when(userService.findOneById(1L)).thenReturn(sampleDto());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUser").value(1L))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john@email.com"));

        verify(userService).findOneById(1L);
    }

    @Test
    void show_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.findOneById(99L)).thenThrow(new UserNotFoundException());
        doReturn(ResponseEntity.notFound().build())
                .when(errorResponseService).buildError(any(Exception.class), any(HttpServletRequest.class), any(ErrorKey.class));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }
}