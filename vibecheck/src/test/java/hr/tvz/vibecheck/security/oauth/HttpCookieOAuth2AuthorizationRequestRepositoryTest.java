package hr.tvz.vibecheck.security.oauth;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieOAuth2AuthorizationRequestRepositoryTest {

    private HttpCookieOAuth2AuthorizationRequestRepository repository;
    private OAuth2AuthorizationRequest authRequest;

    @BeforeEach
    void setUp() {
        repository = new HttpCookieOAuth2AuthorizationRequestRepository();
        authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId("test-client-id")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .redirectUri("http://localhost/callback")
                .scopes(Set.of("profile", "email"))
                .state("test-state")
                .build();
    }

    @Test
    void loadAuthorizationRequest_whenNoCookie_shouldReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        OAuth2AuthorizationRequest result = repository.loadAuthorizationRequest(request);

        assertThat(result).isNull();
    }

    @Test
    void loadAuthorizationRequest_whenCookiePresent_shouldDeserializeRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String serialized = Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(authRequest)
        );
        request.setCookies(new Cookie("oauth2_auth_request", serialized));

        OAuth2AuthorizationRequest result = repository.loadAuthorizationRequest(request);

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("test-client-id");
        assertThat(result.getState()).isEqualTo("test-state");
    }

    @Test
    void saveAuthorizationRequest_withValidRequest_shouldAddCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        repository.saveAuthorizationRequest(authRequest, request, response);

        Cookie cookie = response.getCookie("oauth2_auth_request");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(180);
    }

    @Test
    void saveAuthorizationRequest_withNullRequest_shouldDeleteCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        repository.saveAuthorizationRequest(null, request, response);

        Cookie cookie = response.getCookie("oauth2_auth_request");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isZero();
    }

    @Test
    void removeAuthorizationRequest_shouldReturnLoadedRequestAndDeleteCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String serialized = Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(authRequest)
        );
        request.setCookies(new Cookie("oauth2_auth_request", serialized));

        OAuth2AuthorizationRequest result = repository.removeAuthorizationRequest(request, response);

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("test-client-id");

        Cookie deletedCookie = response.getCookie("oauth2_auth_request");
        assertThat(deletedCookie).isNotNull();
        assertThat(deletedCookie.getMaxAge()).isZero();
    }

    @Test
    void removeAuthorizationRequest_withNoCookie_shouldReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthorizationRequest result = repository.removeAuthorizationRequest(request, response);

        assertThat(result).isNull();
    }

    @Test
    void saveAndLoad_roundTrip_shouldPreserveRequest() {
        MockHttpServletRequest saveRequest = new MockHttpServletRequest();
        MockHttpServletResponse saveResponse = new MockHttpServletResponse();

        repository.saveAuthorizationRequest(authRequest, saveRequest, saveResponse);

        MockHttpServletRequest loadRequest = new MockHttpServletRequest();
        Cookie savedCookie = saveResponse.getCookie("oauth2_auth_request");
        loadRequest.setCookies(savedCookie);

        OAuth2AuthorizationRequest loaded = repository.loadAuthorizationRequest(loadRequest);

        assertThat(loaded).isNotNull();
        assertThat(loaded.getClientId()).isEqualTo(authRequest.getClientId());
        assertThat(loaded.getState()).isEqualTo(authRequest.getState());
    }
}
