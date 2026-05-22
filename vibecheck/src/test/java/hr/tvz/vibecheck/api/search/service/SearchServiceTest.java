package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.spotify.service.SpotifyActionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.liquibase.contexts=integration",
        "spring.datasource.url=jdbc:tc:postgresql:16:///vibecheck",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
@Transactional
class SearchServiceTest {

    @Autowired
    SearchService searchService;
    @MockitoBean
    SpotifyActionsService spotifyService;

    @BeforeEach
    void setUp() {
        when(spotifyService.trackSearch(anyString()))
                .thenReturn(List.of());
    }

    @Test
    @DisplayName("search - vraća korisnike koji odgovaraju keywordu")
    void search_withValidKeyword_returnsMatchingUsers() {
        var results = searchService.search("lsaric");

        assertThat(results)
                .isNotEmpty()
                .allMatch(r -> r.getType().equals("user"))
                .anyMatch(r -> r.getTitleText().equals("lsaric"));
    }

    @Test
    @DisplayName("search - vraća praznu listu za prazan keyword")
    void search_withEmptyKeyword_returnsEmptyList() {
        var results = searchService.search("");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("search - vraća praznu listu kad nema podudaranja")
    void search_withNonMatchingKeyword_returnsEmptyList() {
        var results = searchService.search("xyzxyzxyz_ne_postoji");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("search - SearchResult ima ispravno mapirana polja")
    void search_resultHasCorrectlyMappedFields() {
        var results = searchService.search("lsaric");

        assertThat(results).isNotEmpty();
        var result = results.getFirst();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getType()).isEqualTo("user");
        assertThat(result.getTitleText()).isNotBlank();
        assertThat(result.getSubtitleText()).isNotBlank();
    }
}