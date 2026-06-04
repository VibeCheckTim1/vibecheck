package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.spotify.service.SpotifyActionsService;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchProviderTest {

    @Test
    void userProviderMapsRepositoryUsersToSearchResults() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = User.builder()
                .idUser(1L)
                .username("ana")
                .firstName("Ana")
                .lastName("Anic")
                .avatarUrl("avatar.png")
                .build();
        when(userRepository.searchUsers("ana")).thenReturn(List.of(user));
        UserSearchProvider provider = new UserSearchProvider(userRepository);

        List<SearchResult> results = provider.search("ana");

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.getId()).isEqualTo("1");
            assertThat(result.getType()).isEqualTo("user");
            assertThat(result.getTitleText()).isEqualTo("ana");
            assertThat(result.getSubtitleText()).isEqualTo("Ana Anic");
            assertThat(result.getImageUrl()).isEqualTo("avatar.png");
        });
    }

    @Test
    void spotifyProviderDelegatesTrackSearch() {
        SpotifyActionsService spotifyActionsService = mock(SpotifyActionsService.class);
        SearchResult song = new SearchResult("song-1", "song", "Song", "Artist", null);
        when(spotifyActionsService.trackSearch("song")).thenReturn(List.of(song));
        SpotifySearchProvider provider = new SpotifySearchProvider(spotifyActionsService);

        assertThat(provider.search("song")).containsExactly(song);

        verify(spotifyActionsService).trackSearch("song");
    }
}
