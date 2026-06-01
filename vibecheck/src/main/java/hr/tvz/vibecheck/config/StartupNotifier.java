package hr.tvz.vibecheck.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupNotifier {

    private final DiscordWebhookService discordWebhookService;

    @EventListener(ApplicationReadyEvent.class)
    public void notifyStartup() {
        discordWebhookService.sendStartupNotification();
    }
}
