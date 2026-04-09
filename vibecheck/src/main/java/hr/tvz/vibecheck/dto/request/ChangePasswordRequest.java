package hr.tvz.vibecheck.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(@NotNull String oldPassword, @NotNull String newPassword) {
}
