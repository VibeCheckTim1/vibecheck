package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.config.port.StartupNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupNotifier {

    private final StartupNotificationSender notificationSender;

    @EventListener(ApplicationReadyEvent.class)
    public void notifyStartup() {
        notificationSender.sendStartupNotification();
    }
}
