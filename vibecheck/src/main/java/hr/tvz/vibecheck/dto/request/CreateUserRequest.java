package hr.tvz.vibecheck.dto.request;

import hr.tvz.vibecheck.enums.ProfileVisibility;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        String bio,
        @NotNull ProfileVisibility visibility,
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
