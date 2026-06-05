package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.config.port.ServerStatusNotificationSender;
import hr.tvz.vibecheck.config.port.StartupNotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
public class DiscordWebhookService implements StartupNotificationSender, ServerStatusNotificationSender {

    private final RestClient restClient;
    private final String webhookUrl;
    private final String appName;

    public DiscordWebhookService(
            RestClient restClient,
            @Value("${app.discord.webhook-url:}") String webhookUrl,
            @Value("${spring.application.name:application}") String appName
    ) {
        this.restClient = restClient;
        this.webhookUrl = webhookUrl;
        this.appName = appName;
    }

    @Override
    public void sendStartupNotification() {
        sendMessage("%s - backend has started".formatted(appName), "startup");
    }

    @Override
    public void sendDailyServerStatus() {
        sendMessage("%s - server is up".formatted(appName), "daily server status");
    }

    private void sendMessage(String message, String notificationType) {
        if (!StringUtils.hasText(webhookUrl)) {
            log.info("Discord webhook URL not configured, skipping {} notification", notificationType);
            return;
        }

        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Sent Discord {} notification", notificationType);
        } catch (Exception e) {
            log.warn("Failed to send Discord {} notification", notificationType, e);
        }
    }
}
