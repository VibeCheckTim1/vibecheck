package hr.tvz.vibecheck.api.account.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MailContentFactoryTest {

    private final MailContentFactory factory = new MailContentFactory();

    @Test
    void buildsEmailChangeContentWithCode() {
        assertThat(factory.buildEmailChangeVerificationHtml("123456"))
                .contains("Verify your email", "123456");
    }

    @Test
    void buildsOAuthContentWithPassword() {
        assertThat(factory.buildOAuthPasswordEmail("temporary-password"))
                .contains("Account created via OAuth", "temporary-password");
    }
}
