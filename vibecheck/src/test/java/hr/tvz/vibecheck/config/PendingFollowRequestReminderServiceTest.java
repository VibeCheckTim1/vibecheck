package hr.tvz.vibecheck.config;

import hr.tvz.vibecheck.api.account.service.MailService;
import hr.tvz.vibecheck.quartz.DTO.PendingFollowRequestReminderTarget;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.quartz.service.PendingFollowRequestReminderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
/*This test checks business logic:
When repository returns reminder targets,
send emails in normal mode.
Do not send emails in dry-run mode.
Return processed count.
*/
@ExtendWith(MockitoExtension.class)
class PendingFollowRequestReminderServiceTest {

    @Test
    void sendsReminderEmailsToUsersWithPendingRequests() {
        FollowRequestRepository followRequestRepository =
                mock(FollowRequestRepository.class);

        MailService mailService = mock(MailService.class);

        PendingFollowRequestReminderService service =
                new PendingFollowRequestReminderService(
                        followRequestRepository,
                        mailService
                );

        when(followRequestRepository.findPendingFollowRequestReminderTargets(1))
                .thenReturn(List.of(
                        new PendingFollowRequestReminderTarget(
                                1L,
                                "marko",
                                "marko@test.com",
                                2L
                        ),
                        new PendingFollowRequestReminderTarget(
                                2L,
                                "ana",
                                "ana@test.com",
                                1L
                        )
                ));

        int processedCount =
                service.sendPendingFollowRequestReminders(1, false);

        assertThat(processedCount).isEqualTo(2);

        verify(mailService).sendPendingFollowRequestReminderEmail(
                "marko@test.com",
                "marko",
                2L
        );

        verify(mailService).sendPendingFollowRequestReminderEmail(
                "ana@test.com",
                "ana",
                1L
        );
    }

    @Test
    void dryRunDoesNotSendEmails() {
        FollowRequestRepository followRequestRepository =
                mock(FollowRequestRepository.class);

        MailService mailService = mock(MailService.class);

        PendingFollowRequestReminderService service =
                new PendingFollowRequestReminderService(
                        followRequestRepository,
                        mailService
                );

        when(followRequestRepository.findPendingFollowRequestReminderTargets(1))
                .thenReturn(List.of(
                        new PendingFollowRequestReminderTarget(
                                1L,
                                "marko",
                                "marko@test.com",
                                2L
                        )
                ));

        int processedCount =
                service.sendPendingFollowRequestReminders(1, true);

        assertThat(processedCount).isEqualTo(1);

        verifyNoInteractions(mailService);
    }
}
