package hr.tvz.vibecheck.api.user.dto;

public record UserOutputDto(
        Long idUser,
        String username,
        String firstName,
        String lastName,
        String bio,
        String email,
        String avatarUrl,
        Boolean isPrivate
) {
}
