package hr.tvz.vibecheck.api.account.dto.spotify;

import java.util.List;

public record SpotifyTrackItem(
        String id,
        String name,
        List<SpotifyArtistItem> artists,
        SpotifyAlbumItem album
) {
}
