package hr.tvz.vibecheck.api.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        String bio,
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
