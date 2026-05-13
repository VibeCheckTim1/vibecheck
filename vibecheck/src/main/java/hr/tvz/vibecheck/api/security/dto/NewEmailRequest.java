package hr.tvz.vibecheck.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewEmailRequest(@NotBlank @Email @Schema(defaultValue = "mgradiscaj@gmail.com") String newEmail) {
}
