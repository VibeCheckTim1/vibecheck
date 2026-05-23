package hr.tvz.vibecheck.api.playlists.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlaylistRequestDto(
        @NotBlank @Size(max = 100) String name,
        Boolean isFavorite,
        Boolean isPublic
) {
}
