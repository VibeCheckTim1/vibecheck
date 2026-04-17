package hr.tvz.vibecheck.dto.response;

import java.time.LocalDateTime;

public record FollowRequestResponse(
        Long idRequest,
        Long senderId,
        String senderName,
        String senderLastName,
        LocalDateTime createdAt) {
}
