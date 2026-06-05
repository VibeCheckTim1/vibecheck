package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.api.security.service.RefreshTokenService;
import hr.tvz.vibecheck.quartz.ExpiredRefreshTokenCleanupJob;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpiredRefreshTokenCleanupJobTest {

    @Test
    void executeDeletesExpiredTokensAndUpdatesJobDataMap() throws Exception {
        RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
        ExpiredRefreshTokenCleanupJob job = new ExpiredRefreshTokenCleanupJob(refreshTokenService);
        JobExecutionContext context = mock(JobExecutionContext.class);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("runCount", 4);
        jobDataMap.put("lastDeletedCount", 0);

        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(refreshTokenService.deleteExpiredTokens()).thenReturn(7);

        job.execute(context);

        verify(refreshTokenService).deleteExpiredTokens();

        assertThat(jobDataMap.getInt("runCount")).isEqualTo(5);
        assertThat(jobDataMap.getInt("lastDeletedCount")).isEqualTo(7);
    }
}
