package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.quartz.ExpiredRefreshTokenCleanupJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzRefreshTokenCleanupJobConfig {
    public static final String JOB_NAME = "expiredRefreshTokenCleanupJob";
    public static final String JOB_GROUP = "security";
    public static final String TRIGGER_NAME = "expiredRefreshTokenCleanupTrigger";
    public static final String DATA_RUN_COUNT = "runCount";
    public static final String DATA_LAST_DELETED_COUNT = "lastDeletedCount";
    public static final String EVERY_FIVE_MINUTES_CRON_EXPRESSION = "0 0/5 * * * ?";

    @Bean
    public JobDetail expiredRefreshTokenCleanupJobDetail() {
        return JobBuilder.newJob(ExpiredRefreshTokenCleanupJob.class)
                .withIdentity(JOB_NAME, JOB_GROUP)
                .withDescription("Deletes expired or revoked refresh tokens from the database")
                .usingJobData(DATA_RUN_COUNT, 0)
                .usingJobData(DATA_LAST_DELETED_COUNT, 0)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger expiredRefreshTokenCleanupTrigger(JobDetail expiredRefreshTokenCleanupJobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_NAME, JOB_GROUP)
                .forJob(expiredRefreshTokenCleanupJobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(EVERY_FIVE_MINUTES_CRON_EXPRESSION)
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }
}
