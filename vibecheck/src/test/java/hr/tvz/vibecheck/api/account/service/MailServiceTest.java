package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.exception.custom.MailSendFailedException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailContentFactory mailContentFactory;

    private MailService mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailService(mailSender, mailContentFactory, "noreply@vibecheck.test");
    }

    @Test
    void sendsEmailChangeCodeUsingContentFactory() throws Exception {
        MimeMessage message = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(mailContentFactory.buildEmailChangeVerificationHtml("123456")).thenReturn("<p>123456</p>");

        mailService.sendEmailChangeVerificationCodeEmail("user@test.com", "123456");

        verify(mailContentFactory).buildEmailChangeVerificationHtml("123456");
        verify(mailSender).send(message);
        assertThat(message.getSubject()).isEqualTo("VibeCheck - Email change verification code");
        assertThat(message.getAllRecipients()[0].toString()).hasToString("user@test.com");
    }

    @Test
    void sendsOAuthPasswordUsingContentFactory() throws Exception {
        MimeMessage message = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(mailContentFactory.buildOAuthPasswordEmail("temporary-password"))
                .thenReturn("<p>temporary-password</p>");

        mailService.sendOAuthPasswordEmail("user@test.com", "temporary-password");

        verify(mailContentFactory).buildOAuthPasswordEmail("temporary-password");
        verify(mailSender).send(message);
        assertThat(message.getSubject()).isEqualTo("VibeCheck - OAuth Password");
    }

    @Test
    void wrapsMailSenderFailure() {
        when(mailSender.createMimeMessage()).thenThrow(new IllegalStateException("mail unavailable"));

        assertThatThrownBy(() -> mailService.sendOAuthPasswordEmail("user@test.com", "password"))
                .isInstanceOf(MailSendFailedException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
