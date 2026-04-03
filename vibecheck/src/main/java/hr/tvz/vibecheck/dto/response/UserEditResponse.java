package hr.tvz.vibecheck.dto.response;

import hr.tvz.vibecheck.enums.ProfileVisibility;

import java.time.LocalDateTime;

public record UserEditResponse(
        Long idUser,
        String username,
        String firstName,
        String lastName,
        String email,
        String avatarUrl,
        String bio,
        ProfileVisibility visibility,
        LocalDateTime tstamp
){}
