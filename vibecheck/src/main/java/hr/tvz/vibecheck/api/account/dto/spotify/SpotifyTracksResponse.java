package hr.tvz.vibecheck.api.account.dto.spotify;

import java.util.List;

public record SpotifyTracksResponse(
        List<SpotifyTrackItem> items
) {
}
