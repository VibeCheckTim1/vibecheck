package hr.tvz.vibecheck.api.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequestDto(
        @NotBlank @Email @Schema(defaultValue = "mgradiscaj@gmail.com") String newEmail
) {
}
