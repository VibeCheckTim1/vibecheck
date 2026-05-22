package hr.tvz.vibecheck.api.search.controller;

import hr.tvz.vibecheck.api.account.service.spotify.SpotifyActionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.liquibase.contexts=integration",
        "spring.datasource.url=jdbc:tc:postgresql:16:///vibecheck",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
@AutoConfigureMockMvc
@Transactional
class SearchControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SpotifyActionsService spotifyService;

    @BeforeEach
    void setUp() {
        when(spotifyService.trackSearch(anyString()))
                .thenReturn(List.of());
    }

    @Test
    @DisplayName("GET /search?q=luka - vraća rezultate koji odgovaraju upitu")
    void search_withMatchingKeyword_returnsResults() throws Exception {
        var loginResult = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "username": "lsaric", "password": "1234" }
                            """))
                .andReturn();
        var accessCookie = loginResult.getResponse().getCookie("ACCESS");

        mockMvc.perform(get("/search").param("q", "luka").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titleText").value("lsaric"));
    }

    @Test
    @DisplayName("GET /search?q=xyz - vraća praznu listu")
    void search_withNoMatchingKeyword_returnsEmptyList() throws Exception {
        var loginResult = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "username": "lsaric", "password": "1234" }
                            """))
                .andReturn();
        var accessCookie = loginResult.getResponse().getCookie("ACCESS");

        mockMvc.perform(get("/search").param("q", "xyznonexistent").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /search - 401 bez autentifikacije")
    void search_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/search").param("q", "luka"))
                .andExpect(status().isUnauthorized());
    }
}