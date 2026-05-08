package hr.tvz.vibecheck.api.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /users/{id} - 200 za postojećeg korisnika")
    void show_withExistingUser_returns200() throws Exception {
        var loginResult = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "username": "lsaric", "password": "1234" }
                            """))
                .andReturn();

        var accessCookie = loginResult.getResponse().getCookie("ACCESS");
        mockMvc.perform(get("/users/1").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lsaric"));
    }

    @Test
    @DisplayName("GET /users/{id} - 404 za nepostojećeg korisnika")
    void show_withNonExistingUser_returns404() throws Exception {
        var loginResult = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "username": "lsaric", "password": "1234" }
                            """))
                .andReturn();

        var accessCookie = loginResult.getResponse().getCookie("ACCESS");
        mockMvc.perform(get("/users/9999").cookie(accessCookie))
                .andExpect(status().isNotFound());
    }
}