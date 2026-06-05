package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchServiceUnitTest {

    @Test
    void combinesResultsFromEverySearchProvider() {
        SearchProvider userProvider = mock(SearchProvider.class);
        SearchProvider songProvider = mock(SearchProvider.class);
        SearchResult user = new SearchResult("1", "user", "ana", "Ana Anic", null);
        SearchResult song = new SearchResult("2", "song", "Song", "Artist", null);
        when(userProvider.search("query")).thenReturn(List.of(user));
        when(songProvider.search("query")).thenReturn(List.of(song));
        SearchService searchService = new SearchService(List.of(userProvider, songProvider));

        List<SearchResult> results = searchService.search("  query  ");

        assertThat(results).containsExactly(user, song);
    }

    @Test
    void doesNotCallProvidersForBlankKeyword() {
        SearchProvider provider = mock(SearchProvider.class);
        SearchService searchService = new SearchService(List.of(provider));

        assertThat(searchService.search("   ")).isEmpty();

        verify(provider, never()).search(org.mockito.ArgumentMatchers.anyString());
    }
}
