package hr.tvz.vibecheck.api.account.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyArtistItem(
        String id,
        String name
) {
}
