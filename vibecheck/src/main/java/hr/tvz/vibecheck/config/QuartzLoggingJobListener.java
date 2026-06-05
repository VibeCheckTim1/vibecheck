package hr.tvz.vibecheck.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class QuartzLoggingJobListener implements JobListener {

    private static final String NAME = "quartzLoggingJobListener";

    private final Map<String, Long> startedAtByFireInstanceId = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        startedAtByFireInstanceId.put(
                context.getFireInstanceId(),
                System.currentTimeMillis()
        );

        log.info("Quartz job started: {}", context.getJobDetail().getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        startedAtByFireInstanceId.remove(context.getFireInstanceId());

        log.warn("Quartz job vetoed: {}", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(
            JobExecutionContext context,
            JobExecutionException jobException
    ) {
        JobKey key = context.getJobDetail().getKey();

        long startedAt = startedAtByFireInstanceId.getOrDefault(
                context.getFireInstanceId(),
                System.currentTimeMillis()
        );

        long durationMs = System.currentTimeMillis() - startedAt;

        startedAtByFireInstanceId.remove(context.getFireInstanceId());

        if (jobException == null) {
            log.info("Quartz job finished: {} in {} ms", key, durationMs);
            return;
        }

        log.error("Quartz job failed: {} in {} ms", key, durationMs, jobException);
    }
}