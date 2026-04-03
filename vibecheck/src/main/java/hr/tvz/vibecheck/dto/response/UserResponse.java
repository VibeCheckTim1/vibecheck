package hr.tvz.vibecheck.dto.response;

import hr.tvz.vibecheck.enums.ProfileVisibility;

public record UserResponse(
        Long idUser,
        String username,
        String firstName,
        String lastName,
        String bio,
        String email,
        String avatarUrl,
        ProfileVisibility visibility
) {
}
