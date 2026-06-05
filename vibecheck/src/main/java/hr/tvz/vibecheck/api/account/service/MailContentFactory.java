package hr.tvz.vibecheck.api.account.service;

import org.springframework.stereotype.Component;

@Component
public class MailContentFactory {
    private static final String BODY_STYLE = "margin:0; padding:0; background-color:#f5f3ff; "
            + "font-family:Arial, Helvetica, sans-serif; color:#1f2937;";
    private static final String PAGE_TABLE_STYLE = "background-color:#f5f3ff; padding:40px 16px;";
    private static final String CARD_STYLE = "max-width:560px; background-color:#ffffff; "
            + "border-radius:20px; overflow:hidden;";
    private static final String CARD_SHADOW = " box-shadow:0 8px 24px rgba(0,0,0,0.08);";
    private static final String HEADER_STYLE = "background:linear-gradient(135deg, #8b5cf6, #6d28d9); padding:32px;";
    private static final String CONTENT_CELL_STYLE = "padding:40px; text-align:center;";
    private static final String BRAND_STYLE = "font-size:28px; font-weight:700; color:#ffffff;";
    private static final String PURPLE_VALUE_STYLE = "font-weight:800; color:#6d28d9;";

    public String buildEmailChangeVerificationHtml(String code) {
        return buildEmail(
                "VibeCheck Verification Code",
                false,
                "",
                buildVerificationContent(code),
                ""
        );
    }

    public String buildOAuthPasswordEmail(String password) {
        return buildEmail(
                "VibeCheck Account Setup",
                true,
                "Account created via OAuth",
                buildOAuthPasswordContent(password),
                buildFooter()
        );
    }

    private String buildEmail(
            String pageTitle,
            boolean cardShadow,
            String headerSubtitle,
            String content,
            String footer
    ) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                </head>
                <body style="%s">
                    <table width="100%%" style="%s">
                        <tr>
                            <td align="center">
                                <table style="%s">
                %s
                %s
                %s
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                pageTitle,
                BODY_STYLE,
                PAGE_TABLE_STYLE,
                cardShadow ? CARD_STYLE + CARD_SHADOW : CARD_STYLE,
                buildHeader(headerSubtitle),
                buildContentCell(content),
                footer
        );
    }

    private String buildHeader(String subtitle) {
        var subtitleHtml = subtitle.isBlank()
                ? ""
                : """
                                                  <div style="margin-top:8px; font-size:14px; color:#ede9fe;">%s</div>
                        """.formatted(subtitle);

        return """
                                    <tr>
                                        <td align="center" style="%s">
                                            <div style="%s">VibeCheck</div>
                %s
                                        </td>
                                    </tr>
                """.formatted(HEADER_STYLE, BRAND_STYLE, subtitleHtml);
    }

    private String buildContentCell(String content) {
        return """
                                    <tr>
                                        <td style="%s">
                %s
                                        </td>
                                    </tr>
                """.formatted(CONTENT_CELL_STYLE, content);
    }

    private String buildVerificationContent(String code) {
        return """
                                            <div style="font-size:24px; font-weight:700;">Verify your email</div>
                                            <div style="margin:20px 0;">
                                                <span style="font-size:36px; letter-spacing:8px; %s">
                                                    %s
                                                </span>
                                            </div>
                                            <div style="color:#6b7280;">Code expires in 10 minutes.</div>
                """.formatted(PURPLE_VALUE_STYLE, code);
    }

    private String buildOAuthPasswordContent(String password) {
        return """
                                            <div style="font-size:22px; font-weight:700; margin-bottom:12px;">Your account is ready</div>
                                            <div style="font-size:15px; color:#4b5563; margin-bottom:24px; line-height:24px;">
                                                You signed in using an external provider (Google/Spotify).
                                                We've created a password for your account so you can also log in directly.
                                            </div>
                                            <div style="display:inline-block; background-color:#f3e8ff; border:1px solid #ddd6fe; border-radius:16px; padding:18px 32px; margin-bottom:24px;">
                                                <span style="font-size:24px; letter-spacing:4px; %s">
                                                    %s
                                                </span>
                                            </div>
                                            <div style="font-size:14px; color:#ef4444; font-weight:600; margin-bottom:12px;">
                                                You must change this password after logging in
                                            </div>
                                            <div style="font-size:14px; color:#6b7280; line-height:22px;">
                                                For security reasons, please log in and update your password immediately in your profile settings.
                                            </div>
                """.formatted(PURPLE_VALUE_STYLE, password);
    }

    private String buildFooter() {
        return """
                                    <tr>
                                        <td style="padding:24px; background-color:#faf5ff; text-align:center; border-top:1px solid #ede9fe;">
                                            <div style="font-size:12px; color:#8b5cf6; font-weight:600;">VibeCheck</div>
                                            <div style="font-size:12px; color:#6b7280;">This is an automated message, please do not reply.</div>
                                        </td>
                                    </tr>
                """;
    }
}
