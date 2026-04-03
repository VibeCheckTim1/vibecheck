package hr.tvz.vibecheck.dto.response;

import hr.tvz.vibecheck.enums.ProfileVisibility;

public record UserEditResponse(
        Long idUser,
        String username,
        String firstName,
        String lastName,
        String bio,
        ProfileVisibility visibility
) {
}
