package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.account.service.spotify.SpotifyActionsService;
import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserRepository userRepository;
    private final SpotifyActionsService spotifyService;


    public List<SearchResult> search(String keyword) {
        List<SearchResult> results = new ArrayList<>();
        String trimmedKeyword = keyword.trim();

        if (!trimmedKeyword.isEmpty()) {
            // korisnici
            List<User> users = userRepository.searchUsers(trimmedKeyword);
            for (User user : users) {
                results.add(mapUserToDto(user));
            }

            //pjesme
            List<SearchResult> searchedSongs = spotifyService.trackSearch(trimmedKeyword);
            results.addAll(searchedSongs);

        }

        return results;
    }

    private SearchResult mapUserToDto(User user) {
        return new SearchResult(
                String.valueOf(user.getIdUser()),
                "user",
                user.getUsername(),
                user.getFirstName() + " " + user.getLastName(),
                user.getAvatarUrl()
        );
    }
}
