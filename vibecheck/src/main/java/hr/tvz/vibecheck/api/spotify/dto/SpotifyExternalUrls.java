package hr.tvz.vibecheck.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyExternalUrls(
     @JsonProperty("spotify") String spotifyUrl
) {
}
