package hr.tvz.vibecheck.api.account.dto;

import java.time.LocalDateTime;

public record FollowRequestResponse(
        Long idRequest,
        Long senderId,
        String senderName,
        String senderLastName,
        String avatarUrl,
        LocalDateTime createdAt) {
}
