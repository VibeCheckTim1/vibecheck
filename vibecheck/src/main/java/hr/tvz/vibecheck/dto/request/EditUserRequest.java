package hr.tvz.vibecheck.dto.request;

import hr.tvz.vibecheck.enums.ProfileVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String bio,
        @NotNull ProfileVisibility visibility
) {
}
