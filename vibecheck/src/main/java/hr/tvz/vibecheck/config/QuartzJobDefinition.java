package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.config.port.ServerStatusNotificationSender;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.util.function.Function;

@Getter
public enum QuartzJobDefinition {

    DISCORD_SERVER_STATUS(
            "discordServerStatusJob",
            "discord",
            "discordServerStatusTrigger",
            "discord",
            applicationContext -> () -> applicationContext
                    .getBean(ServerStatusNotificationSender.class)
                    .sendDailyServerStatus(),
            "0 0 0 * * ?"
    );

    private final String jobName;
    private final String jobGroup;
    private final String triggerName;
    private final String triggerGroup;
    private final Function<ApplicationContext, Runnable> runnableFactory;
    private final String cronExpression;

    QuartzJobDefinition(
            String jobName,
            String jobGroup,
            String triggerName,
            String triggerGroup,
            Function<ApplicationContext, Runnable> runnableFactory,
            String cronExpression
    ) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.runnableFactory = runnableFactory;
        this.cronExpression = cronExpression;
    }

    public Runnable createRunnable(ApplicationContext applicationContext) {
        return runnableFactory.apply(applicationContext);
    }

    public JobKey jobKey() {
        return JobKey.jobKey(jobName, jobGroup);
    }

    public TriggerKey triggerKey() {
        return TriggerKey.triggerKey(triggerName, triggerGroup);
    }
}
