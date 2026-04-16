package hr.tvz.vibecheck.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(example = "lsaric")
        @NotBlank String username,
        @Schema(example = "1234")
        @NotBlank String password
) {}
