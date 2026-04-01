package hr.tvz.vibecheck.dto.request;

import hr.tvz.vibecheck.entity.enum_.ProfileVisibility;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        String bio,
        ProfileVisibility visibility,
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
