package hr.tvz.vibecheck.api.playlists.controller;

import hr.tvz.vibecheck.api.playlists.dto.CreatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.dto.PlaylistResponseDto;
import hr.tvz.vibecheck.api.playlists.dto.UpdatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.service.PlaylistService;
import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.exception.custom.NotYourRequestException;
import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PlaylistController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class PlaylistControllerTest {

    @TestConfiguration
    static class TestMvcConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaylistService playlistService;

    @MockitoBean
    private ErrorResponseService errorResponseService;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private PlaylistResponseDto sampleDto() {
        return new PlaylistResponseDto(1L, "Playlist", true, true, 0, 1L, "lsaric");
    }

    @Test
    @WithVibeCheckUser(id = 1L)
    void create_shouldReturn201WithPlaylist_whenRequestIsValid() throws Exception {
        when(playlistService.create(eq(1L), any(CreatePlaylistRequestDto.class))).thenReturn(sampleDto());

        mockMvc.perform(post("/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Playlist",
                                  "isFavorite": true,
                                  "isPublic": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Playlist"))
                .andExpect(jsonPath("$.isFavorite").value(true))
                .andExpect(jsonPath("$.isPublic").value(true));

        verify(playlistService).create(eq(1L), any(CreatePlaylistRequestDto.class));
    }

    @Test
    @WithVibeCheckUser(id = 2L)
    void indexByUser_shouldReturn200WithUserPlaylists() throws Exception {
        when(playlistService.findByUserId(1L, 2L)).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/users/1/playlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(playlistService).findByUserId(1L, 2L);
    }

    @Test
    @WithVibeCheckUser(id = 2L)
    void show_shouldReturn200WithPlaylist_whenRequesterCanViewPlaylist() throws Exception {
        when(playlistService.findOne(1L, 2L)).thenReturn(sampleDto());

        mockMvc.perform(get("/playlists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("lsaric"));

        verify(playlistService).findOne(1L, 2L);
    }

    @Test
    @WithVibeCheckUser(id = 2L)
    void show_shouldReturn403_whenRequesterCannotViewPlaylist() throws Exception {
        when(playlistService.findOne(2L, 2L)).thenThrow(new NotYourRequestException("Forbidden"));
        doReturn(ResponseEntity.status(403).build())
                .when(errorResponseService).buildError(any(Exception.class), any(HttpServletRequest.class), any(ErrorKey.class));

        mockMvc.perform(get("/playlists/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithVibeCheckUser(id = 1L)
    void update_shouldReturn200WithUpdatedPlaylist_whenRequesterIsOwner() throws Exception {
        when(playlistService.update(eq(1L), eq(1L), any(UpdatePlaylistRequestDto.class)))
                .thenReturn(new PlaylistResponseDto(1L, "Updated", false, false, 0, 1L, "lsaric"));

        mockMvc.perform(patch("/playlists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated",
                                  "isFavorite": false,
                                  "isPublic": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.isPublic").value(false));

        verify(playlistService).update(eq(1L), eq(1L), any(UpdatePlaylistRequestDto.class));
    }

    @Test
    @WithVibeCheckUser(id = 1L)
    void delete_shouldReturn204_whenRequesterIsOwner() throws Exception {
        mockMvc.perform(delete("/playlists/1"))
                .andExpect(status().isNoContent());

        verify(playlistService).delete(1L, 1L);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithVibeCheckUserSecurityContextFactory.class)
    public @interface WithVibeCheckUser {
        long id();
    }

    public static class WithVibeCheckUserSecurityContextFactory implements WithSecurityContextFactory<WithVibeCheckUser> {
        @Override
        public org.springframework.security.core.context.SecurityContext createSecurityContext(WithVibeCheckUser annotation) {
            var principal = VibeCheckUserDetails.builder()
                    .id(annotation.id())
                    .username("lsaric")
                    .email("lsaric@vibecheck.hr")
                    .roles(List.of("USER"))
                    .build();
            var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            return context;
        }
    }
}
