package hr.tvz.vibecheck.quartz.controller;
import hr.tvz.vibecheck.config.QuartzEmailJobConfig;
import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminQuartzJobController {
    private static final String PENDING_FOLLOW_REQUEST_EMAIL_JOB =
            "pending-follow-request-email";
    private final Scheduler scheduler;


    @PostMapping("/{name}/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> triggerJob(@PathVariable String name) throws SchedulerException {
        JobKey jobKey = resolveJobKey(name);

        scheduler.triggerJob(jobKey);

        return ResponseEntity.ok(Map.of(
                "status", "triggered",
                "job", jobKey.toString()
        ));
    }


    private JobKey resolveJobKey(String name) {
        if (PENDING_FOLLOW_REQUEST_EMAIL_JOB.equals(name)) {
            return JobKey.jobKey(
                    QuartzEmailJobConfig.JOB_NAME,
                    QuartzEmailJobConfig.JOB_GROUP
            );
        }
        throw new ResponseStatusException(
                NOT_FOUND,
                "Unknown Quartz job: " + name
        );
    }
}
