package hr.tvz.vibecheck.api.security.entity;

import hr.tvz.vibecheck.api.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthAccountTest {

    @Test
    void builder_shouldCreateOAuthAccountWithAllFields() {
        User user = User.builder().idUser(1L).username("testuser").build();

        OAuthAccount account = OAuthAccount.builder()
                .idOAuthAccount(10L)
                .user(user)
                .provider("google")
                .providerUserId("google-user-123")
                .accessToken("access-token-value")
                .refreshToken("refresh-token-value")
                .build();

        assertThat(account.getIdOAuthAccount()).isEqualTo(10L);
        assertThat(account.getUser()).isEqualTo(user);
        assertThat(account.getProvider()).isEqualTo("google");
        assertThat(account.getProviderUserId()).isEqualTo("google-user-123");
        assertThat(account.getAccessToken()).isEqualTo("access-token-value");
        assertThat(account.getRefreshToken()).isEqualTo("refresh-token-value");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyAccount() {
        OAuthAccount account = new OAuthAccount();

        assertThat(account.getIdOAuthAccount()).isNull();
        assertThat(account.getProvider()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateAccount() {
        User user = User.builder().username("user").build();

        OAuthAccount account = new OAuthAccount(1L, user, "spotify", "spotify-123", "acc-token", "ref-token");

        assertThat(account.getIdOAuthAccount()).isEqualTo(1L);
        assertThat(account.getProvider()).isEqualTo("spotify");
        assertThat(account.getProviderUserId()).isEqualTo("spotify-123");
    }

    @Test
    void setters_shouldUpdateFields() {
        OAuthAccount account = new OAuthAccount();
        User user = User.builder().username("newuser").build();

        account.setIdOAuthAccount(99L);
        account.setUser(user);
        account.setProvider("google");
        account.setProviderUserId("google-456");
        account.setAccessToken("new-access");
        account.setRefreshToken("new-refresh");

        assertThat(account.getIdOAuthAccount()).isEqualTo(99L);
        assertThat(account.getUser()).isEqualTo(user);
        assertThat(account.getProvider()).isEqualTo("google");
        assertThat(account.getProviderUserId()).isEqualTo("google-456");
        assertThat(account.getAccessToken()).isEqualTo("new-access");
        assertThat(account.getRefreshToken()).isEqualTo("new-refresh");
    }
}
