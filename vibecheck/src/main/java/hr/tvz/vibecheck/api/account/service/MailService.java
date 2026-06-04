package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.exception.custom.MailSendFailedException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final MailContentFactory mailContentFactory;
    private final String fromEmail;

    public MailService(
            JavaMailSender mailSender,
            MailContentFactory mailContentFactory,
            @Value("${spring.mail.username}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.mailContentFactory = mailContentFactory;
        this.fromEmail = fromEmail;
    }

    public void sendEmailChangeVerificationCodeEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("VibeCheck - Email change verification code");
            helper.setText(mailContentFactory.buildEmailChangeVerificationHtml(code), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email change verification code email", e);
            throw new MailSendFailedException("Failed to send email change verification code email", e);
        }
    }

    public void sendOAuthPasswordEmail(String to, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("VibeCheck - OAuth Password");
            helper.setText(mailContentFactory.buildOAuthPasswordEmail(password), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OAuth password email", e);
            throw new MailSendFailedException("Failed to send OAuth password email", e);
        }
    }
}
