package hr.tvz.vibecheck.quartz.service;

import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.service.MailService;
import hr.tvz.vibecheck.quartz.DTO.PendingFollowRequestReminderTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingFollowRequestReminderService {
    private final FollowRequestRepository followRequestRepository;
    private final MailService mailService;

    @Transactional(readOnly = true)
    public int sendPendingFollowRequestReminders(int minPendingCount, boolean dryRun) {
        if (minPendingCount < 1) {
            throw new IllegalArgumentException("minPendingCount must be at least 1");
        }

        List<PendingFollowRequestReminderTarget> targets =
                followRequestRepository.findPendingFollowRequestReminderTargets(minPendingCount);

        int processedCount = 0;

        for(PendingFollowRequestReminderTarget target : targets) {
            if (target.email() == null || target.email().isBlank()) {
                log.warn("Skipping reminder for user {} because email is missing", target.userId());
                continue;
            }

            if (dryRun) {
                log.info(
                        "Dry run: would send pending follow request reminder to {} for {} request(s)",
                        target.email(),
                        target.pendingCount()
                );
            }
            else {
                mailService.sendPendingFollowRequestReminderEmail(
                        target.email(),
                        target.username(),
                        target.pendingCount()
                );
            }
            processedCount++;
        }
        log.info("Processed {} pending follow request reminder(s)", processedCount);

        return processedCount;

    }

}
