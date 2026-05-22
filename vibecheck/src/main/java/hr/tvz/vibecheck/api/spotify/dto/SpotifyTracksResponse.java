package hr.tvz.vibecheck.api.spotify.dto;

import java.util.List;

public record SpotifyTracksResponse(
        List<SpotifyTrackItem> items
) {
}
