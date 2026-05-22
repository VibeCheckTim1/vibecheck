package hr.tvz.vibecheck.api.spotify.controller;

import hr.tvz.vibecheck.api.spotify.entity.UserLikedSong;
import hr.tvz.vibecheck.api.spotify.service.SpotifyActionsService;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/songs")
public class SpotifyActionsController {
    private final SpotifyActionsService spotifySongService;

    @PostMapping("/{spotifySongId}/like")
    public ResponseEntity<Void> likeSong(
            @PathVariable String spotifySongId,
            @AuthenticationPrincipal @NonNull VibeCheckUserDetails userDetails
    ) {
        UserLikedSong s = spotifySongService.addSongToLikedSongs(userDetails.getId(), spotifySongId);

        return ResponseEntity.ok().build();
    }
}
