package hr.tvz.vibecheck.api.account.dto;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequestDto(@NotNull String oldPassword, @NotNull String newPassword) {
}
