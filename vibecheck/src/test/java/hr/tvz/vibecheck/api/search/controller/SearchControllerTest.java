package hr.tvz.vibecheck.api.search.controller;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.search.service.SearchService;
import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import hr.tvz.vibecheck.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SearchController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class SearchControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SearchService searchService;

    @MockitoBean
    AccessTokenService accessTokenService;

    @MockitoBean
    ErrorResponseService errorResponseService;

    @MockitoBean
    UserDetailsService userDetailsService;

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
    @DisplayName("GET /search - 200 s rezultatima za poznati keyword")
    void search_withMatchingKeyword_returns200AndResults() throws Exception {
        var results = List.of(
                new SearchResult("1", "user", "kstjepanovic", "Karlo Stjepanović", null),
                new SearchResult("2", "user", "mhorvat", "Marko Horvat", null)
        );
        when(searchService.search("luk")).thenReturn(results);

        mockMvc.perform(get("/search").param("q", "luk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("user"))
                .andExpect(jsonPath("$[0].titleText").value("kstjepanovic"))
                .andExpect(jsonPath("$[0].subtitleText").value("Karlo Stjepanović"));
    }

    @Test
    @DisplayName("GET /search - 200 s praznom listom kad nema rezultata")
    void search_withNoMatches_returns200AndEmptyList() throws Exception {
        when(searchService.search("zzznepronalazi")).thenReturn(List.of());

        mockMvc.perform(get("/search").param("q", "zzznepronalazi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /search - 200 s praznom listom kad je keyword prazan string")
    void search_withEmptyKeyword_returns200AndEmptyList() throws Exception {
        when(searchService.search("")).thenReturn(List.of());

        mockMvc.perform(get("/search").param("q", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}