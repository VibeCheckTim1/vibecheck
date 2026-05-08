package hr.tvz.vibecheck.api.account.controller;

import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.account.dto.FollowRequestRequest;
import hr.tvz.vibecheck.api.account.dto.FollowStatsResponse;
import hr.tvz.vibecheck.api.account.service.FollowRequestService;
import hr.tvz.vibecheck.api.security.enums.FollowActionResult;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import hr.tvz.vibecheck.security.SecurityConfig;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import hr.tvz.vibecheck.security.jwt.JwtFilter;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import hr.tvz.vibecheck.exception.framework.ErrorKey;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


@WebMvcTest(
        controllers = FollowRequestController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
        }

)
public class FollowRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FollowRequestService followRequestService;

    @MockitoBean
    private ErrorResponseService errorResponseService;


    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }



    @Test
    @DisplayName("POST /followRequest return 200 OK when request is valid")
    void shouldReturnOk_WhenRequestIsValid() throws Exception {
        Long currentUserId = 1L;

        when(followRequestService.createFollowRequestOrFollow(eq(currentUserId), any(FollowRequestRequest.class)))
                .thenReturn(new FollowActionResponse(FollowActionResult.PENDING));

        mockMvc.perform(post("/followRequest")
                        .with(authentication(authenticatedUser(currentUserId)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "receiverId": 2
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PENDING"));

        verify(followRequestService).createFollowRequestOrFollow(eq(currentUserId), any(FollowRequestRequest.class));
    }

    @Test
    @DisplayName("POST /followRequest returns 400 Bad Request when request body is invalid")
    void shouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        Long currentUserId = 1L;

        when(errorResponseService.buildError(
                any(Exception.class),
                any(HttpServletRequest.class),
                any(ErrorKey.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/followRequest")
                        .with(authentication(authenticatedUser(currentUserId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "receiverId": null
                            }
                            """))
                .andExpect(status().isBadRequest());

        verify(followRequestService, never())
                .createFollowRequestOrFollow(anyLong(), any(FollowRequestRequest.class));
    }


    @Test
    @DisplayName("DELETE /followRequest/{receiverId} returns 204 No Content")
    void shouldReturnNoContent_WhenCancelFollowRequestIsValid() throws Exception {
        Long currentUserId = 1L;
        Long receiverId = 2L;

        doNothing().when(followRequestService).cancelFollowRequest(currentUserId, receiverId);

        mockMvc.perform(delete("/followRequest/{receiverId}", receiverId)
                .with(authentication(authenticatedUser(currentUserId))))
                .andExpect(status().isNoContent());

        verify(followRequestService).cancelFollowRequest(currentUserId, receiverId);
    }

    @Test
    @DisplayName("DELETE /followRequest/unfollow/{receiverId} returns 204 No Content")
    void shouldReturnNoContentWhenUnfollowIsValid() throws Exception {
        Long currentUserId = 1L;
        Long receiverId = 2L;

        doNothing().when(followRequestService).unfollow(currentUserId, receiverId);

        mockMvc.perform(delete("/followRequest/unfollow/{receiverId}", receiverId)
        .with(authentication(authenticatedUser(currentUserId))))
                .andExpect(status().isNoContent());

        verify(followRequestService).unfollow(currentUserId, receiverId);
    }

    @Test
    @DisplayName("GET /followRequest/getFollowStatus/{receiverId} returns 200 OK")
    void shouldReturnOkWhenGettingFollowStatus() throws Exception {
        Long currentUserId = 1L;
        Long receiverId = 2L;

        when(followRequestService.getFollowStatus(currentUserId, receiverId))
                .thenReturn(new FollowActionResponse(FollowActionResult.FOLLOWING));

        mockMvc.perform(get("/followRequest/getFollowStatus/{receiverId}", receiverId)
                .with(authentication(authenticatedUser(currentUserId))))
                .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result").value("FOLLOWING"));

        verify(followRequestService).getFollowStatus(currentUserId, receiverId);
    }

    @Test
    @DisplayName("PATCH /followRequest/acceptOrDeclineFollowRequest/{requestId} returns 200 OK")
    void shouldReturnOkWhenAcceptOrDeclineRequestIsValid() throws Exception {
        Long currentUserId = 1L;
        Long requestId = 2L;

        doNothing().when(followRequestService)
                .acceptOrDeclineFollowRequest(eq(requestId), eq(currentUserId), any());

        mockMvc.perform((patch("/followRequest/acceptOrDeclineFollowRequest/{requestId}", requestId))
                .with(authentication(authenticatedUser(currentUserId)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                         {
                            "action": "ACCEPT"
                         }
                        """
                ))
                .andExpect(status().isOk());

        verify(followRequestService).acceptOrDeclineFollowRequest(eq(requestId), eq(currentUserId), any());
    }


    @Test
    @DisplayName("GET /followRequest/stats returns 200 OK")
    void shouldReturnOkWhenGettingFollowStats() throws Exception {
        Long currentUserId = 1L;

        when(followRequestService.getFollowStats(currentUserId))
                .thenReturn(new FollowStatsResponse(5L, 6L));

        mockMvc.perform(get("/followRequest/stats")
                .with(authentication(authenticatedUser(currentUserId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followersCount").value(5L))
                .andExpect(jsonPath("$.followingCount").value(6L));

        verify(followRequestService).getFollowStats(currentUserId);

    }

    private UsernamePasswordAuthenticationToken authenticatedUser(Long userId) {
        VibeCheckUserDetails userDetails = mock(VibeCheckUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
    }

}
