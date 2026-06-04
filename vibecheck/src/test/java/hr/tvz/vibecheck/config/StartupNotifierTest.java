package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.config.port.StartupNotificationSender;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StartupNotifierTest {

    @Test
    void notifiesStartupThroughAbstraction() {
        StartupNotificationSender notificationSender = mock(StartupNotificationSender.class);
        StartupNotifier notifier = new StartupNotifier(notificationSender);

        notifier.notifyStartup();

        verify(notificationSender).sendStartupNotification();
    }
}
