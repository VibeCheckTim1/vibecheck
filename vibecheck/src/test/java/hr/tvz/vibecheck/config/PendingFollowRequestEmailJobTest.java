package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.quartz.PendingFollowRequestEmailJob;
import hr.tvz.vibecheck.quartz.service.PendingFollowRequestReminderService;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
/*This test checks:
The job reads JobDataMap.
The job calls the service with correct values.
The job updates runCount and lastSentCount.
* */
class PendingFollowRequestEmailJobTest {

    @Test
    void executeCallsReminderServiceAndUpdatesJobDataMap() throws Exception {
        PendingFollowRequestReminderService reminderService =
                mock(PendingFollowRequestReminderService.class);

        PendingFollowRequestEmailJob job =
                new PendingFollowRequestEmailJob(reminderService);

        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDetail jobDetail = mock(JobDetail.class);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("minPendingCount", 2);
        jobDataMap.put("dryRun", true);
        jobDataMap.put("runCount", 4);
        jobDataMap.put("lastSentCount", 0);

        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);

        when(reminderService.sendPendingFollowRequestReminders(2, true))
                .thenReturn(3);

        job.execute(context);

        verify(reminderService).sendPendingFollowRequestReminders(2, true);

        assertThat(jobDataMap.getInt("runCount")).isEqualTo(5);
        assertThat(jobDataMap.getInt("lastSentCount")).isEqualTo(3);
    }
}
