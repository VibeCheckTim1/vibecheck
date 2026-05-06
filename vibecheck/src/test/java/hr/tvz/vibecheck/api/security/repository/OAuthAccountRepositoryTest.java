package hr.tvz.vibecheck.api.security.repository;

import hr.tvz.vibecheck.api.security.entity.OAuthAccount;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OAuthAccountRepositoryTest {

    @Autowired
    OAuthAccountRepository oAuthAccountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        User savedUser = userRepository.save(User.builder()
                .firstName("OAuth")
                .lastName("User")
                .username("oauthuser")
                .email("oauth@test.com")
                .password("pass")
                .avatarUrl("avatar.jpg")
                .roles(new ArrayList<>())
                .tstamp(LocalDateTime.now())
                .build());
        oAuthAccountRepository.save(OAuthAccount.builder()
                .user(savedUser)
                .provider("google")
                .providerUserId("google-123")
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build());
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findByProviderAndProviderUserId vraća account kada provider i id postoje")
    void findByProviderAndProviderUserId_returnsAccount() {
        Optional<OAuthAccount> result = oAuthAccountRepository.findByProviderAndProviderUserId("google", "google-123");

        assertThat(result).isPresent();
        assertThat(result.get().getProvider()).isEqualTo("google");
        assertThat(result.get().getProviderUserId()).isEqualTo("google-123");
        assertThat(result.get().getUser().getUsername()).isEqualTo("oauthuser");
    }

    @Test
    @DisplayName("findByProviderAndProviderUserId vraća empty za krivi provider")
    void findByProviderAndProviderUserId_emptyWhenWrongProvider() {
        Optional<OAuthAccount> result = oAuthAccountRepository.findByProviderAndProviderUserId("github", "google-123");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByProviderAndProviderUserId vraća empty za krivu providerUserId")
    void findByProviderAndProviderUserId_emptyWhenWrongProviderUserId() {
        Optional<OAuthAccount> result = oAuthAccountRepository.findByProviderAndProviderUserId("google", "nepostoji-999");

        assertThat(result).isEmpty();
    }
}
