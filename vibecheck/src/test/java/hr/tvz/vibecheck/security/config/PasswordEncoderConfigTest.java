package hr.tvz.vibecheck.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderConfigTest {

    private final PasswordEncoderConfig config = new PasswordEncoderConfig();

    @Test
    void passwordEncoder_shouldReturnBCryptEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void passwordEncoder_shouldEncodeAndMatchPassword() {
        PasswordEncoder encoder = config.passwordEncoder();
        String rawPassword = "testPassword123";

        String encoded = encoder.encode(rawPassword);

        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
        assertThat(encoder.matches("wrongPassword", encoded)).isFalse();
    }

    @Test
    void passwordEncoder_shouldProduceDifferentEncodings() {
        PasswordEncoder encoder = config.passwordEncoder();
        String rawPassword = "samePassword";

        String hash1 = encoder.encode(rawPassword);
        String hash2 = encoder.encode(rawPassword);

        assertThat(hash1).isNotEqualTo(hash2);
    }
}
