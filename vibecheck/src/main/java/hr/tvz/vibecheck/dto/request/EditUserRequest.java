package hr.tvz.vibecheck.dto.request;

import hr.tvz.vibecheck.entity.enum_.ProfileVisibility;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        String avatarUrl,
        String avatarPublicId,
        String bio,
        @NotNull ProfileVisibility visibility
        //@Email @NotBlank String email,
        //@NotBlank String password
) {
}
