package hr.tvz.vibecheck.api.security.dto;

import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import jakarta.validation.constraints.NotNull;

public record FollowRequestActionRequest(@NotNull FollowRequestUserResponse action) {
}
