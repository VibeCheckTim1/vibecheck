package hr.tvz.vibecheck.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record FollowRequestRequest(@Schema(example = "3") @NotNull Long receiverId) {

}
