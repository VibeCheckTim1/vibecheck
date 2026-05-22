package hr.tvz.vibecheck.api.account.service.spotify;

import hr.tvz.vibecheck.api.account.dto.spotify.*;
import hr.tvz.vibecheck.api.search.dto.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.sound.midi.Track;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotifyActionsService {
    private final SpotifyAppTokenService spotifyAppTokenService;
    private final RestClient restClient;

    public List<SearchResult> trackSearch(String keyword) {
        String accessToken = spotifyAppTokenService.getAccessToken();

        SpotifySearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/search")
                        .queryParam("q", keyword)
                        .queryParam("type", "track")
                        .queryParam("limit", 5)
                        .queryParam("offset", 0)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(SpotifySearchResponse.class);

        if (response == null || response.tracks() == null || response.tracks().items() == null) {
            return List.of();
        }

        return response.tracks().items().stream()
                .map(this::mapTrackItemToSearchResult)
                .toList();

    }

    private SearchResult mapTrackItemToSearchResult(SpotifyTrackItem item) {
        String artists = item.artists().stream()
                .map(SpotifyArtistItem::name)
                .collect(Collectors.joining(","));

        return new SearchResult(
                item.id(),
                "song",
                item.name(),
                artists,
                getAlbumImageUrl(item.album())
        );
    }

    private String getAlbumImageUrl(SpotifyAlbumItem album) {
        if (album == null || album.images() == null || album.images().isEmpty()) {
            return null;
        }

        return album.images().stream()
                .filter(image -> image.height() != null && image.width() != null)
                .filter(image -> image.height() == 300 && image.width() == 300)
                .map(SpotifyImageItem::url)
                .findFirst()
                .orElse(album.images().getFirst().url());
    }



}
