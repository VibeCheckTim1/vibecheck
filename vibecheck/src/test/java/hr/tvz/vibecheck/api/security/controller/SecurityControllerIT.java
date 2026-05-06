package hr.tvz.vibecheck.api.security.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.liquibase.contexts=integration",
        "spring.datasource.url=jdbc:tc:postgresql:16:///vibecheck",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
@AutoConfigureMockMvc
@Transactional
class SecurityControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("POST /security/login - 200 s validnim kredencijalima i postavljeni kolačići")
    void login_withValidCredentials_returns200AndSetsCookies() throws Exception {
        mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "lsaric", "password": "1234" }
                                """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("ACCESS"))
                .andExpect(cookie().exists("REFRESH"));
    }

    @Test
    @DisplayName("POST /security/login - greška s pogrešnim passwordom")
    void login_withWrongPassword_returnsError() throws Exception {
        mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "lsaric", "password": "wrongpassword" }
                                """))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /security/register - 200 s novim korisnikom i postavljeni kolačići")
    void register_withNewUser_returns200AndSetsCookies() throws Exception {
        mockMvc.perform(post("/security/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Test",
                                  "lastName": "User",
                                  "username": "testuser_it",
                                  "email": "testuser@it.hr",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("ACCESS"))
                .andExpect(cookie().exists("REFRESH"));
    }

    @Test
    @DisplayName("POST /security/logout - 204 i kolačići obrisani")
    void logout_returns204AndClearsCookies() throws Exception {
        var loginResult = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "lsaric", "password": "1234" }
                                """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("ACCESS"))
                .andExpect(cookie().exists("REFRESH"))
                .andReturn();

        var accessCookie = loginResult.getResponse().getCookie("ACCESS");
        var refreshCookie = loginResult.getResponse().getCookie("REFRESH");

        assert accessCookie != null;
        assert refreshCookie != null;

        mockMvc.perform(post("/security/logout")
                        .cookie(accessCookie)
                        .cookie(refreshCookie))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("ACCESS", 0))
                .andExpect(cookie().maxAge("REFRESH", 0));
    }
}
