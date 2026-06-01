package hr.tvz.vibecheck.config;

import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuartzJobSchedulerServiceTest {

    @Test
    void schedulesQuartzJobsFromDefinitions() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        DiscordWebhookService discordWebhookService = mock(DiscordWebhookService.class);
        QuartzJobDefinition jobDefinition = QuartzJobDefinition.DISCORD_SERVER_STATUS;

        when(applicationContext.getBean(DiscordWebhookService.class)).thenReturn(discordWebhookService);

        QuartzJobSchedulerService service = new QuartzJobSchedulerService(
                scheduler,
                applicationContext
        );

        service.scheduleConfiguredJobs();

        verify(scheduler).scheduleJob(
                eqJobDetail(jobDefinition),
                eqTrigger(jobDefinition, jobDefinition.getCronExpression())
        );
    }

    private JobDetail eqJobDetail(QuartzJobDefinition jobDefinition) {
        return org.mockito.ArgumentMatchers.argThat(jobDetail ->
                jobDetail.getKey().equals(jobDefinition.jobKey())
        );
    }

    private Trigger eqTrigger(QuartzJobDefinition jobDefinition, String cronExpression) {
        return org.mockito.ArgumentMatchers.argThat(trigger ->
                trigger.getKey().equals(jobDefinition.triggerKey())
                        && ((CronTrigger) trigger).getCronExpression().equals(cronExpression)
        );
    }
}
