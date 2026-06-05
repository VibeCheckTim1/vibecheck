package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.quartz.PendingFollowRequestEmailJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class QuartzEmailJobConfig {
    public static final String JOB_NAME = "pendingFollowRequestEmailJob";
    public static final String JOB_GROUP = "email";

    public static final String CRON_TRIGGER_NAME = "pendingFollowRequestEmailCronTrigger";
    public static final String SIMPLE_TRIGGER_NAME = "pendingFollowRequestEmailSimpleTrigger";

    public static final String DATA_MIN_PENDING_COUNT = "minPendingCount";
    public static final String DATA_DRY_RUN = "dryRun";
    public static final String DATA_RUN_COUNT = "runCount";
    public static final String DATA_LAST_SENT_COUNT = "lastSentCount";

    public static final String ZAGREB_TIME_ZONE = "Europe/Zagreb";
    public static final String DAILY_CRON_EXPRESSION = "0 0 9 * * ?";


    @Bean
    public JobDetail pendingFollowRequestEmailJobDetail() {
        return JobBuilder.newJob(PendingFollowRequestEmailJob.class)
                .withIdentity(JOB_NAME, JOB_GROUP)
                .withDescription("Sends reminder emails to users with pending follow requests")
                .usingJobData(DATA_MIN_PENDING_COUNT, 1)
                .usingJobData(DATA_DRY_RUN, false)
                .usingJobData(DATA_RUN_COUNT, 0)
                .usingJobData(DATA_LAST_SENT_COUNT, 0)
                .storeDurably()
                .build();
    }


    @Bean
    public Trigger pendingFollowRequestEmailCronTrigger(JobDetail pendingFollowRequestEmailJobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(CRON_TRIGGER_NAME, JOB_GROUP)
                .forJob(pendingFollowRequestEmailJobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(DAILY_CRON_EXPRESSION)
                        .inTimeZone(TimeZone.getTimeZone(ZoneId.of(ZAGREB_TIME_ZONE)))
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }

    @Bean
    public Trigger pendingFollowRequestEmailSimpleTrigger(JobDetail pendingFollowRequestEmailJobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(SIMPLE_TRIGGER_NAME, JOB_GROUP)
                .forJob(pendingFollowRequestEmailJobDetail)
                .startAt(DateBuilder.futureDate(30, DateBuilder.IntervalUnit.SECOND))
                .usingJobData(DATA_DRY_RUN, true)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withRepeatCount(0)
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }


}
