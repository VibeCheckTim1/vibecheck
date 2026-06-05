package hr.tvz.vibecheck.quartz.DTO;

public record PendingFollowRequestReminderTarget(
        Long userId,
        String username,
        String email,
        Long pendingCount
) {
}
