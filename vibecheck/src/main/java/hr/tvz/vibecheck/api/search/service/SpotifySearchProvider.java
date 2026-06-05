package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.spotify.service.SpotifyActionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class SpotifySearchProvider implements SearchProvider {
    private final SpotifyActionsService spotifyActionsService;

    @Override
    public List<SearchResult> search(String keyword) {
        return spotifyActionsService.trackSearch(keyword);
    }
}
