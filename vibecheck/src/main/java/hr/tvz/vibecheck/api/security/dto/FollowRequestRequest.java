package hr.tvz.vibecheck.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record FollowRequestRequest(@Schema(example = "3") @NotNull Long receiverId) {

}
