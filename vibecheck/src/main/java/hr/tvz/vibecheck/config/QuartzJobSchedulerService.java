package hr.tvz.vibecheck.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzJobSchedulerService {

    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleConfiguredJobs() {
        for (QuartzJobDefinition jobDefinition : QuartzJobDefinition.values()) {
            schedule(jobDefinition);
        }
    }

    private void schedule(QuartzJobDefinition jobDefinition) {
        try {
            JobDetail jobDetail = createJobDetail(jobDefinition);
            Trigger trigger = createTrigger(jobDefinition, jobDetail);

            if (scheduler.checkExists(jobDefinition.jobKey())) {
                scheduler.deleteJob(jobDefinition.jobKey());
            }

            scheduler.scheduleJob(jobDetail, trigger);
            log.info(
                    "Scheduled Quartz job {} with trigger {} using cron {}",
                    jobDefinition.jobKey(),
                    jobDefinition.triggerKey(),
                    jobDefinition.getCronExpression()
            );
        } catch (Exception e) {
            log.warn("Failed to schedule Quartz job {}", jobDefinition.getJobName(), e);
        }
    }

    private JobDetail createJobDetail(QuartzJobDefinition jobDefinition) throws Exception {
        MethodInvokingJobDetailFactoryBean factoryBean = new MethodInvokingJobDetailFactoryBean();
        factoryBean.setName(jobDefinition.getJobName());
        factoryBean.setGroup(jobDefinition.getJobGroup());
        factoryBean.setTargetObject(jobDefinition.createRunnable(applicationContext));
        factoryBean.setTargetMethod("run");
        factoryBean.setConcurrent(false);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    private Trigger createTrigger(QuartzJobDefinition jobDefinition, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobDefinition.triggerKey())
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobDefinition.getCronExpression()))
                .build();
    }
}
