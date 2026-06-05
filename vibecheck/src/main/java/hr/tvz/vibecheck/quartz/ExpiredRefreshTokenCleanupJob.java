package hr.tvz.vibecheck.quartz;

import hr.tvz.vibecheck.api.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class ExpiredRefreshTokenCleanupJob implements Job {
    private final RefreshTokenService refreshTokenService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        int runCount = dataMap.containsKey("runCount") ? dataMap.getInt("runCount") : 0;

        try {
            int deletedCount = refreshTokenService.deleteExpiredOrRevokedTokens();

            dataMap.put("runCount", runCount + 1);
            dataMap.put("lastDeletedCount", deletedCount);

            log.info(
                    "Expired or revoked refresh token cleanup job finished. deletedCount={}, runCount={}",
                    deletedCount,
                    runCount + 1
            );
        }
        catch (Exception e) {
            log.error("Expired or revoked refresh token cleanup job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
