package hr.tvz.vibecheck.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.EverythingMatcher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJobListenerRegistration {

    private final Scheduler scheduler;
    private final QuartzLoggingJobListener quartzLoggingJobListener;

    @EventListener(ApplicationReadyEvent.class)
    public void registerQuartzLoggingJobListener() {
        try {
            scheduler.getListenerManager()
                    .addJobListener(
                            quartzLoggingJobListener,
                            EverythingMatcher.allJobs()
                    );

            log.info("Registered Quartz job listener: {}", quartzLoggingJobListener.getName());
        } catch (Exception e) {
            log.warn("Failed to register Quartz job listener", e);
        }
    }
}