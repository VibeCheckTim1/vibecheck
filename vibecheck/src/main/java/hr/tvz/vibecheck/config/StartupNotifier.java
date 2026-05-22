package hr.tvz.vibecheck.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupNotifier {

    private final RestClient restClient;

    @Value("${app.discord.webhook-url:}")
    private String webhookUrl;

    @Value("${spring.application.name:application}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    public void notifyStartup() {
        if (!StringUtils.hasText(webhookUrl)) {
            log.info("Discord webhook URL not configured, skipping startup notification");
            return;
        }

        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("content", "✅ %s - backend has started".formatted(appName)))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Sent Discord startup notification");
        } catch (Exception e) {
            log.warn("Failed to send Discord startup notification", e);
        }
    }
}