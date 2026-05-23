package hr.tvz.vibecheck.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyImageItem(
        String url,
        Integer height,
        Integer width
) {

}
