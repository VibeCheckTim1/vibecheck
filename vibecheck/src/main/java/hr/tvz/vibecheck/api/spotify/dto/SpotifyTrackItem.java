package hr.tvz.vibecheck.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTrackItem(
        String id,
        String name,
        List<SpotifyArtistItem> artists,
        SpotifyAlbumItem album,
        @JsonProperty("duration_ms") Integer durationMs,
        @JsonProperty("external_urls")SpotifyExternalUrls externalUrls
) {
}
