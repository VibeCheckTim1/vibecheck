package hr.tvz.vibecheck.api.playlists.dto;

public record PlaylistResponseDto(
        Long id,
        String name,
        Boolean isFavorite,
        Boolean isPublic,
        Integer songCount,
        Long userId,
        String username
) {
}
