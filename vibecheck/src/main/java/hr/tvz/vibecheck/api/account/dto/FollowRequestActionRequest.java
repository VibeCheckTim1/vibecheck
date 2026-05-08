package hr.tvz.vibecheck.dto.request;

import hr.tvz.vibecheck.enums.FollowRequestUserResponse;
import jakarta.validation.constraints.NotNull;

public record FollowRequestActionRequest(@NotNull FollowRequestUserResponse action) {
}
