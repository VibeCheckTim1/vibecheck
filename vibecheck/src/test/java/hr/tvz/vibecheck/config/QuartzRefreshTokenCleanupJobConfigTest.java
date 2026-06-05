package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.quartz.ExpiredRefreshTokenCleanupJob;
import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import static org.assertj.core.api.Assertions.assertThat;

class QuartzRefreshTokenCleanupJobConfigTest {

    private final QuartzRefreshTokenCleanupJobConfig config = new QuartzRefreshTokenCleanupJobConfig();

    @Test
    void createsExpiredRefreshTokenCleanupJobDetail() {
        JobDetail jobDetail = config.expiredRefreshTokenCleanupJobDetail();

        assertThat(jobDetail.getKey().getName())
                .isEqualTo("expiredRefreshTokenCleanupJob");

        assertThat(jobDetail.getKey().getGroup())
                .isEqualTo("security");

        assertThat(jobDetail.getJobClass())
                .isEqualTo(ExpiredRefreshTokenCleanupJob.class);

        assertThat(jobDetail.getJobDataMap().getInt("runCount"))
                .isZero();

        assertThat(jobDetail.getJobDataMap().getInt("lastDeletedCount"))
                .isZero();
    }

    @Test
    void createsCronTriggerThatRunsEveryFiveMinutes() {
        JobDetail jobDetail = config.expiredRefreshTokenCleanupJobDetail();

        Trigger trigger = config.expiredRefreshTokenCleanupTrigger(jobDetail);

        assertThat(trigger).isInstanceOf(CronTrigger.class);
        assertThat(trigger.getKey().getName())
                .isEqualTo("expiredRefreshTokenCleanupTrigger");
        assertThat(trigger.getKey().getGroup())
                .isEqualTo("security");

        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertThat(cronTrigger.getCronExpression())
                .isEqualTo("0 0/5 * * * ?");
    }
}
