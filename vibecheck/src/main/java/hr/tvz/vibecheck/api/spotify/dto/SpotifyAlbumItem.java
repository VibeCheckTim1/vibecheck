package hr.tvz.vibecheck.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbumItem(
        List<SpotifyImageItem> images,
        String name
) {

}
