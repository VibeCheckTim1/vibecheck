package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.quartz.PendingFollowRequestEmailJob;
import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import static org.assertj.core.api.Assertions.assertThat;

/*
This test checks that config creates:
JobDetail
CronTrigger
SimpleTrigger
*/
class QuartzEmailJobConfigTest {

    private final QuartzEmailJobConfig config = new QuartzEmailJobConfig();

    @Test
    void createsPendingFollowRequestEmailJobDetail() {
        JobDetail jobDetail = config.pendingFollowRequestEmailJobDetail();

        assertThat(jobDetail.getKey().getName())
                .isEqualTo("pendingFollowRequestEmailJob");

        assertThat(jobDetail.getKey().getGroup())
                .isEqualTo("email");

        assertThat(jobDetail.getJobClass())
                .isEqualTo(PendingFollowRequestEmailJob.class);

        assertThat(jobDetail.getJobDataMap().getInt("minPendingCount"))
                .isEqualTo(1);

        assertThat(jobDetail.getJobDataMap().getBoolean("dryRun"))
                .isFalse();
    }

    @Test
    void createsCronTrigger() {
        JobDetail jobDetail = config.pendingFollowRequestEmailJobDetail();

        Trigger trigger = config.pendingFollowRequestEmailCronTrigger(jobDetail);

        assertThat(trigger).isInstanceOf(CronTrigger.class);
        assertThat(trigger.getKey().getName())
                .isEqualTo("pendingFollowRequestEmailCronTrigger");

        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertThat(cronTrigger.getCronExpression())
                .isEqualTo("0 0 9 * * ?");

        assertThat(cronTrigger.getTimeZone().getID())
                .isEqualTo("Europe/Zagreb");
    }

    @Test
    void createsSimpleTrigger() {
        JobDetail jobDetail = config.pendingFollowRequestEmailJobDetail();

        Trigger trigger = config.pendingFollowRequestEmailSimpleTrigger(jobDetail);

        assertThat(trigger).isInstanceOf(SimpleTrigger.class);
        assertThat(trigger.getKey().getName())
                .isEqualTo("pendingFollowRequestEmailSimpleTrigger");

        assertThat(trigger.getJobDataMap().getBoolean("dryRun"))
                .isTrue();
    }
}
