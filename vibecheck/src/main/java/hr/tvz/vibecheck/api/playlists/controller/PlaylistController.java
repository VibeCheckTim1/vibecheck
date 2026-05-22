package hr.tvz.vibecheck.api.playlists.controller;

import hr.tvz.vibecheck.api.playlists.dto.CreatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.dto.PlaylistResponseDto;
import hr.tvz.vibecheck.api.playlists.dto.UpdatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.service.PlaylistService;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping("/playlists")
    public ResponseEntity<PlaylistResponseDto> create(
            @RequestBody @Valid CreatePlaylistRequestDto request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playlistService.create(userDetails.getId(), request));
    }

    @GetMapping("/users/{userId}/playlists")
    public ResponseEntity<List<PlaylistResponseDto>> indexByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) {
        return ResponseEntity.ok(playlistService.findByUserId(userId, userDetails.getId()));
    }

    @GetMapping("/playlists/{playlistId}")
    public ResponseEntity<PlaylistResponseDto> show(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) {
        return ResponseEntity.ok(playlistService.findOne(playlistId, userDetails.getId()));
    }

    @PatchMapping("/playlists/{playlistId}")
    public ResponseEntity<PlaylistResponseDto> update(
            @PathVariable Long playlistId,
            @RequestBody @Valid UpdatePlaylistRequestDto request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) {
        return ResponseEntity.ok(playlistService.update(playlistId, userDetails.getId(), request));
    }

    @DeleteMapping("/playlists/{playlistId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) {
        playlistService.delete(playlistId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
