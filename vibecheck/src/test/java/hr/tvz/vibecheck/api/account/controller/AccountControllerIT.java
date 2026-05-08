package hr.tvz.vibecheck.api.account.controller;

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
class AccountControllerIT {

    @Autowired
    MockMvc mockMvc;

    private String loginAndGetCookie() throws Exception {
        var result = mockMvc.perform(post("/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "lsaric", "password": "1234" }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        var cookie = result.getResponse().getCookie("ACCESS");
        assert cookie != null;
        return cookie.getValue();
    }

    @Test
    @DisplayName("PATCH /account - 200 s validnim podacima i autentifikacijom")
    void updateAccount_withValidData_returns200() throws Exception {
        var accessCookie = loginAndGetCookie();

        mockMvc.perform(patch("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new jakarta.servlet.http.Cookie("ACCESS", accessCookie))
                        .content("""
                                {
                                  "firstName": "Luka",
                                  "lastName": "Saric",
                                  "username": "lsaric",
                                  "bio": "updated bio",
                                  "isPrivate": false
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /account - 401 bez autentifikacije")
    void updateAccount_withoutAuth_returns401() throws Exception {
        mockMvc.perform(patch("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Luka",
                                  "lastName": "Saric",
                                  "username": "lsaric",
                                  "bio": "bio",
                                  "isPrivate": false
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /account/password - 204 s ispravnim passwordom")
    void changePassword_withValidData_returns204() throws Exception {
        var accessCookie = loginAndGetCookie();

        mockMvc.perform(patch("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new jakarta.servlet.http.Cookie("ACCESS", accessCookie))
                        .content("""
                            {
                              "oldPassword": "1234",
                              "newPassword": "newpassword123"
                            }
                            """))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /account - 204 i kolačići obrisani")
    void deleteAccount_withAuth_returns204() throws Exception {
        var accessCookie = loginAndGetCookie();

        mockMvc.perform(delete("/account")
                        .cookie(new jakarta.servlet.http.Cookie("ACCESS", accessCookie)))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("ACCESS", 0))
                .andExpect(cookie().maxAge("REFRESH", 0));
    }
}