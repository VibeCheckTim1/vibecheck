package hr.tvz.vibecheck.quartz;

import hr.tvz.vibecheck.quartz.service.PendingFollowRequestReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class PendingFollowRequestEmailJob implements Job {
    private final PendingFollowRequestReminderService reminderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();

        int minPendingCount = dataMap.getInt("minPendingCount");
        boolean dryRun = dataMap.getBoolean("dryRun");

        int runCount = dataMap.containsKey("runCount") ? dataMap.getInt("runCount") : 0;

        try {
            int sentCount = reminderService.sendPendingFollowRequestReminders(minPendingCount, dryRun);

            dataMap.put("runCount", runCount + 1);
            dataMap.put("lastSentCount", sentCount);

            log.info(
                    "Pending follow request email job finished. sentCount={}, runCount={}",
                    sentCount,
                    runCount + 1
            );
        }
        catch (Exception e) {
            log.error("Pending follow request email job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
