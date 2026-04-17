package hr.tvz.vibecheck.api.security.dto;

public record TokenOutputDto(
        String accessToken,
        String refreshToken
) {
}
