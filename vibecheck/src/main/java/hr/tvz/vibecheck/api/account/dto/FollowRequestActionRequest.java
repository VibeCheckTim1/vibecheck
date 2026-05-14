package hr.tvz.vibecheck.api.account.dto;

import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import jakarta.validation.constraints.NotNull;

public record FollowRequestActionRequest(@NotNull FollowRequestUserResponse action) {
}
