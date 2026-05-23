package hr.tvz.vibecheck.api.playlists.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.liquibase.contexts=integration",
        "spring.datasource.url=jdbc:tc:postgresql:16:///vibecheck",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
@AutoConfigureMockMvc
@Transactional
class PlaylistControllerIT {

    @Autowired
    MockMvc mockMvc;

    private Cookie loginAndGetAccessCookie(String username) throws Exception {
        var result = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "%s", "password": "1234" }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn();

        var cookie = result.getResponse().getCookie("ACCESS");
        assert cookie != null;
        return cookie;
    }

    @Test
    @DisplayName("GET /users/{id}/playlists - vlasnik vidi javne i privatne playliste")
    void indexByUser_whenRequesterIsOwner_returnsAllPlaylists() throws Exception {
        var accessCookie = loginAndGetAccessCookie("lsaric");

        mockMvc.perform(get("/users/1/playlists").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    @DisplayName("GET /users/{id}/playlists - drugi korisnik vidi samo javne playliste")
    void indexByUser_whenRequesterIsNotOwner_returnsOnlyPublicPlaylists() throws Exception {
        var accessCookie = loginAndGetAccessCookie("mgradiscaj");

        mockMvc.perform(get("/users/1/playlists").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Luka public playlist"))
                .andExpect(jsonPath("$[0].isPublic").value(true));
    }

    @Test
    @DisplayName("GET /playlists/{id} - javna playlista je vidljiva drugom korisniku")
    void show_withPublicPlaylistAndNonOwner_returns200() throws Exception {
        var accessCookie = loginAndGetAccessCookie("mgradiscaj");

        mockMvc.perform(get("/playlists/1").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luka public playlist"))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @DisplayName("GET /playlists/{id} - privatna playlista nije vidljiva drugom korisniku")
    void show_withPrivatePlaylistAndNonOwner_returns403() throws Exception {
        var accessCookie = loginAndGetAccessCookie("mgradiscaj");

        mockMvc.perform(get("/playlists/2").cookie(accessCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /playlists - autentificirani korisnik moze kreirati playlistu")
    void create_withAuthenticatedUser_returns201() throws Exception {
        var accessCookie = loginAndGetAccessCookie("lsaric");

        mockMvc.perform(post("/playlists")
                        .cookie(accessCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Created from test",
                                  "isFavorite": true,
                                  "isPublic": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Created from test"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    @DisplayName("PATCH /playlists/{id} - vlasnik moze urediti playlistu")
    void update_whenRequesterIsOwner_returns200() throws Exception {
        var accessCookie = loginAndGetAccessCookie("lsaric");

        mockMvc.perform(patch("/playlists/1")
                        .cookie(accessCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated public playlist",
                                  "isFavorite": false,
                                  "isPublic": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated public playlist"))
                .andExpect(jsonPath("$.isFavorite").value(false))
                .andExpect(jsonPath("$.isPublic").value(false));
    }

    @Test
    @DisplayName("PATCH /playlists/{id} - drugi korisnik ne moze urediti playlistu")
    void update_whenRequesterIsNotOwner_returns403() throws Exception {
        var accessCookie = loginAndGetAccessCookie("mgradiscaj");

        mockMvc.perform(patch("/playlists/1")
                        .cookie(accessCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Forbidden update",
                                  "isFavorite": false,
                                  "isPublic": true
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /playlists/{id} - vlasnik moze obrisati playlistu")
    void delete_whenRequesterIsOwner_returns204() throws Exception {
        var accessCookie = loginAndGetAccessCookie("lsaric");

        mockMvc.perform(delete("/playlists/1").cookie(accessCookie))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /playlists/{id} - drugi korisnik ne moze obrisati playlistu")
    void delete_whenRequesterIsNotOwner_returns403() throws Exception {
        var accessCookie = loginAndGetAccessCookie("mgradiscaj");

        mockMvc.perform(delete("/playlists/1").cookie(accessCookie))
                .andExpect(status().isForbidden());
    }
}
