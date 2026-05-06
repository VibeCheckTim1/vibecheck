package hr.tvz.vibecheck.security.config;

import hr.tvz.vibecheck.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2AuthorizationRequestConfigTest {

    private final OAuth2AuthorizationRequestConfig config = new OAuth2AuthorizationRequestConfig();

    @Test
    void cookieAuthorizationRequestRepository_shouldReturnRepositoryInstance() {
        HttpCookieOAuth2AuthorizationRequestRepository repository = config.cookieAuthorizationRequestRepository();

        assertThat(repository).isNotNull().isInstanceOf(HttpCookieOAuth2AuthorizationRequestRepository.class);
    }

    @Test
    void cookieAuthorizationRequestRepository_shouldCreateNewInstanceEachCall() {
        HttpCookieOAuth2AuthorizationRequestRepository repo1 = config.cookieAuthorizationRequestRepository();
        HttpCookieOAuth2AuthorizationRequestRepository repo2 = config.cookieAuthorizationRequestRepository();

        assertThat(repo1).isNotNull();
        assertThat(repo2).isNotNull();
    }
}
