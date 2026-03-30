package hr.tvz.vibecheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "lsaric@vibecheck.hr")
        @NotBlank String email,
        @Schema(example = "1234")
        @NotBlank String password
) {}
