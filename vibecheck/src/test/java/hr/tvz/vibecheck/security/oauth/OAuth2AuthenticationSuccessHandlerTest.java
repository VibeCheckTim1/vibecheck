package hr.tvz.vibecheck.security.oauth;

import hr.tvz.vibecheck.api.security.entity.OAuthAccount;
import hr.tvz.vibecheck.api.security.entity.RefreshToken;
import hr.tvz.vibecheck.api.security.repository.OAuthAccountRepository;
import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import hr.tvz.vibecheck.api.security.service.RefreshTokenService;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock private OAuth2AuthorizedClientService authorizedClientService;
    @Mock private OAuthAccountRepository oAuthAccountRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccessTokenService accessTokenService;
    @Mock private RefreshTokenService refreshTokenService;

    private OAuth2AuthenticationSuccessHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OAuth2AuthenticationSuccessHandler(
                authorizedClientService, oAuthAccountRepository, userRepository,
                accessTokenService, refreshTokenService
        );
        ReflectionTestUtils.setField(handler, "frontendBaseUrl", "http://localhost:3000");
    }

    private VibeCheckUserDetails buildUserDetails() {
        return VibeCheckUserDetails.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("pass")
                .roles(List.of("USER"))
                .build();
    }

    private ClientRegistration buildClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/callback")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://accounts.google.com/o/oauth2/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .scope("profile")
                .build();
    }

    @Test
    void onAuthenticationSuccess_withOAuth2TokenAndRefreshToken_shouldUpdateOAuthAccount() throws IOException {
        VibeCheckUserDetails userDetails = buildUserDetails();
        ClientRegistration clientReg = buildClientRegistration();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "access-token", Instant.now(), Instant.now().plusSeconds(3600)
        );
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token-value", Instant.now());
        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(clientReg, "testuser", accessToken, refreshToken);

        OAuth2AuthenticationToken auth = mock(OAuth2AuthenticationToken.class);
        when(auth.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(auth.getName()).thenReturn("testuser");
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authorizedClientService.loadAuthorizedClient("google", "testuser")).thenReturn(client);

        OAuthAccount oauthAccount = OAuthAccount.builder()
                .provider("google")
                .providerUserId("testuser")
                .build();
        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "testuser"))
                .thenReturn(Optional.of(oauthAccount));

        User user = User.builder().idUser(1L).build();
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(accessTokenService.generateToken(userDetails)).thenReturn("new-access-token");
        when(accessTokenService.generateTokenCookie("new-access-token")).thenReturn(new Cookie("ACCESS", "new-access-token"));

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token("new-refresh-token")
                .build();
        when(refreshTokenService.create(any(), anyString())).thenReturn(refreshTokenEntity);
        when(refreshTokenService.generateTokenCookie("new-refresh-token")).thenReturn(new Cookie("REFRESH", "new-refresh-token"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, auth);

        verify(oAuthAccountRepository).save(oauthAccount);
        assertThat(oauthAccount.getRefreshToken()).isEqualTo("refresh-token-value");
        assertThat(response.getRedirectedUrl()).contains("/home");
    }

    @Test
    void onAuthenticationSuccess_withOAuth2TokenButNoRefreshToken_shouldNotUpdateRefreshToken() throws IOException {
        VibeCheckUserDetails userDetails = buildUserDetails();
        ClientRegistration clientReg = buildClientRegistration();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "access-token", Instant.now(), Instant.now().plusSeconds(3600)
        );
        OAuth2AuthorizedClient clientWithoutRefresh = new OAuth2AuthorizedClient(clientReg, "testuser", accessToken, null);

        OAuth2AuthenticationToken auth = mock(OAuth2AuthenticationToken.class);
        when(auth.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(auth.getName()).thenReturn("testuser");
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authorizedClientService.loadAuthorizedClient("google", "testuser")).thenReturn(clientWithoutRefresh);

        User user = User.builder().idUser(1L).build();
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(accessTokenService.generateToken(userDetails)).thenReturn("new-access-token");
        when(accessTokenService.generateTokenCookie("new-access-token")).thenReturn(new Cookie("ACCESS", "new-access-token"));
        RefreshToken refreshTokenEntity = RefreshToken.builder().token("rt").build();
        when(refreshTokenService.create(any(), anyString())).thenReturn(refreshTokenEntity);
        when(refreshTokenService.generateTokenCookie("rt")).thenReturn(new Cookie("REFRESH", "rt"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, auth);

        verify(oAuthAccountRepository, never()).save(any());
    }

    @Test
    void onAuthenticationSuccess_whenClientIsNull_shouldNotUpdateRefreshToken() throws IOException {
        VibeCheckUserDetails userDetails = buildUserDetails();

        OAuth2AuthenticationToken auth = mock(OAuth2AuthenticationToken.class);
        when(auth.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(auth.getName()).thenReturn("testuser");
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authorizedClientService.loadAuthorizedClient("google", "testuser")).thenReturn(null);

        User user = User.builder().idUser(1L).build();
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(accessTokenService.generateToken(userDetails)).thenReturn("token");
        when(accessTokenService.generateTokenCookie("token")).thenReturn(new Cookie("ACCESS", "token"));
        RefreshToken rt = RefreshToken.builder().token("rt").build();
        when(refreshTokenService.create(any(), anyString())).thenReturn(rt);
        when(refreshTokenService.generateTokenCookie("rt")).thenReturn(new Cookie("REFRESH", "rt"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, auth);

        verify(oAuthAccountRepository, never()).save(any());
    }

    @Test
    void onAuthenticationSuccess_whenPrincipalIsNotVibeCheckUserDetails_shouldNotRedirect() throws IOException {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "not-vibe-check-user", null, List.of()
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, auth);

        assertThat(response.getRedirectedUrl()).isNull();
        verifyNoInteractions(accessTokenService, refreshTokenService, userRepository);
    }
}
