package hr.tvz.vibecheck.api.account.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String bio,
        Boolean isPrivate
) {
}
