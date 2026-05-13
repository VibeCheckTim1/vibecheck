package hr.tvz.vibecheck.api.account.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    public void sendEmailChangeVerificationCodeEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("VibeCheck - Email change verification code");
            helper.setText(buildEmailChangeVerificationHtml(code), true);

            mailSender.send(message);

        }
        catch (Exception e) {
            log.error("Failed to send email change verification code email", e);
            throw new RuntimeException("Failed to send emial change verification code email", e);

        }
    }

    public void sendOAuthPasswordEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("VibeCheck - OAuth Password");
            helper.setText(buildOAuthPasswordEmail(code), true);

            mailSender.send(message);

        }
        catch (Exception e) {
            log.error("Failed to send OAuth password email", e);
            throw new RuntimeException("Failed to send OAuth password email", e);

        }
    }



    private String buildEmailChangeVerificationHtml(String code) {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>VibeCheck Verification Code</title>
        </head>
        <body style="margin:0; padding:0; background-color:#f5f3ff; font-family:Arial, Helvetica, sans-serif; color:#1f2937;">
            <table width="100%" style="background-color:#f5f3ff; padding:40px 16px;">
                <tr>
                    <td align="center">
                        <table style="max-width:560px; background-color:#ffffff; border-radius:20px; overflow:hidden;">\s
                            <tr>
                                <td align="center" style="background:linear-gradient(135deg, #8b5cf6, #6d28d9); padding:32px;">
                                    <div style="font-size:28px; font-weight:700; color:#ffffff;">
                                        VibeCheck
                                    </div>
                                </td>
                            </tr>

                            <tr>
                                <td style="padding:40px; text-align:center;">
                                    <div style="font-size:24px; font-weight:700;">
                                        Verify your email
                                    </div>

                                    <div style="margin:20px 0;">
                                        <span style="font-size:36px; font-weight:800; letter-spacing:8px; color:#6d28d9;">
                                           \s""" + code + """
                                        </span>
                                    </div>

                                    <div style="color:#6b7280;">
                                        Code expires in 10 minutes.
                                    </div>
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;
    }


    public String buildOAuthPasswordEmail(String password) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>VibeCheck Account Setup</title>
                </head>
                <body style="margin:0; padding:0; background-color:#f5f3ff; font-family:Arial, Helvetica, sans-serif; color:#1f2937;">
                    <table width="100%" style="background-color:#f5f3ff; padding:40px 16px;">
                        <tr>
                            <td align="center">
                                <table style="max-width:560px; background-color:#ffffff; border-radius:20px; overflow:hidden; box-shadow:0 8px 24px rgba(0,0,0,0.08);">
                                    <!-- HEADER -->
                                    <tr>
                                        <td align="center" style="background:linear-gradient(135deg, #8b5cf6, #6d28d9); padding:32px;">
                                            <div style="font-size:28px; font-weight:700; color:#ffffff;">
                                                VibeCheck
                                            </div>
                                            <div style="margin-top:8px; font-size:14px; color:#ede9fe;">
                                                Account created via OAuth
                                            </div>
                                        </td>
                                    </tr>
                
                                    <!-- CONTENT -->
                                    <tr>
                                        <td style="padding:40px; text-align:center;">
                
                                            <div style="font-size:22px; font-weight:700; margin-bottom:12px;">
                                                Your account is ready 🎉
                                            </div>
                
                                            <div style="font-size:15px; color:#4b5563; margin-bottom:24px; line-height:24px;">
                                                You signed in using an external provider (Google/Spotify).  
                                                We've created a password for your account so you can also log in directly.
                                            </div>
                
                                            <!-- PASSWORD BOX -->
                                            <div style="display:inline-block; background-color:#f3e8ff; border:1px solid #ddd6fe; border-radius:16px; padding:18px 32px; margin-bottom:24px;">
                                                <span style="font-size:24px; font-weight:800; letter-spacing:4px; color:#6d28d9;">
                """ + password + """
                                                </span>
                                            </div>
                
                                            <div style="font-size:14px; color:#ef4444; font-weight:600; margin-bottom:12px;">
                                                ⚠ You must change this password after logging in
                                            </div>
                
                                            <div style="font-size:14px; color:#6b7280; line-height:22px;">
                                                For security reasons, please log in and update your password immediately in your profile settings.
                                            </div>
                
                                        </td>
                                    </tr>
                
                                    <!-- FOOTER -->
                                    <tr>
                                        <td style="padding:24px; background-color:#faf5ff; text-align:center; border-top:1px solid #ede9fe;">
                                            <div style="font-size:12px; color:#8b5cf6; font-weight:600;">
                                                VibeCheck
                                            </div>
                                            <div style="font-size:12px; color:#6b7280;">
                                                This is an automated message, please do not reply.
                                            </div>
                                        </td>
                                    </tr>
                
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """;
    }

}
