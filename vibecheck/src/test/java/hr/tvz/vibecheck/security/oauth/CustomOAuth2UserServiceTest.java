package hr.tvz.vibecheck.security.oauth;

import hr.tvz.vibecheck.api.account.service.MailService;
import hr.tvz.vibecheck.api.security.entity.OAuthAccount;
import hr.tvz.vibecheck.api.security.entity.Role;
import hr.tvz.vibecheck.api.security.repository.OAuthAccountRepository;
import hr.tvz.vibecheck.api.security.repository.RoleRepository;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestOperations;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private OAuthAccountRepository oAuthAccountRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MailService mailService;
    @Mock private RestOperations restOperations;

    private CustomOAuth2UserService service;

    @BeforeEach
    void setUp() {
        service = new CustomOAuth2UserService(
                userRepository, oAuthAccountRepository, roleRepository, passwordEncoder, mailService
        );
        service.setRestOperations(restOperations);
    }

    private ClientRegistration buildClientRegistration(String registrationId) {
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId("test-client")
                .clientSecret("test-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/callback")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://accounts.google.com/o/oauth2/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .scope("profile")
                .build();
    }

    private OAuth2UserRequest buildUserRequest(String registrationId) {
        ClientRegistration reg = buildClientRegistration(registrationId);
        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "access-token",
                Instant.now(), Instant.now().plusSeconds(3600)
        );
        return new OAuth2UserRequest(reg, token);
    }

    @SuppressWarnings("unchecked")
    private void mockRestOperationsResponse(Map<String, Object> attributes) {
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(attributes, HttpStatus.OK);
        when(restOperations.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
    }

    @Test
    void loadUser_whenOAuthAccountExists_shouldReturnExistingUser() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "google-user-123");
        attrs.put("email", "existing@test.com");
        mockRestOperationsResponse(attrs);

        User user = User.builder().idUser(1L).username("existinguser").email("existing@test.com").build();
        OAuthAccount account = OAuthAccount.builder()
                .user(user).provider("google").providerUserId("google-user-123").build();

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "google-user-123"))
                .thenReturn(Optional.of(account));
        when(oAuthAccountRepository.save(any())).thenReturn(account);
        when(roleRepository.findAllByUserId(1L)).thenReturn(List.of());

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isInstanceOf(VibeCheckUserDetails.class);
        VibeCheckUserDetails details = (VibeCheckUserDetails) result;
        assertThat(details.getUsername()).isEqualTo("existinguser");
        verify(oAuthAccountRepository).save(account);
    }

    @Test
    void loadUser_whenOAuthAccountNotExistsButEmailExists_shouldLinkExistingUser() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "google-new-id");
        attrs.put("email", "linked@test.com");
        attrs.put("given_name", "Linked");
        attrs.put("family_name", "User");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "google-new-id"))
                .thenReturn(Optional.empty());

        User existingUser = User.builder().idUser(2L).username("linkeduser").email("linked@test.com").build();
        when(userRepository.findByEmail("linked@test.com")).thenReturn(Optional.of(existingUser));
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(2L)).thenReturn(List.of());

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isInstanceOf(VibeCheckUserDetails.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void loadUser_whenOAuthAccountNotExistsAndEmailNotExists_shouldRegisterNewUser() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "new-google-id");
        attrs.put("email", "newuser@test.com");
        attrs.put("given_name", "New");
        attrs.put("family_name", "User");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "new-google-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User savedUser = User.builder().idUser(3L).username("newu").email("newuser@test.com").build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(3L)).thenReturn(List.of(Role.builder().name("USER").build()));

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isInstanceOf(VibeCheckUserDetails.class);
        verify(userRepository).save(any(User.class));
        verify(mailService).sendOAuthPasswordEmail(eq("newuser@test.com"), anyString());
    }

    @Test
    void loadUser_whenNoGivenName_shouldUseNameAttribute() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "spotify-id-123");
        attrs.put("email", "spotify@test.com");
        attrs.put("name", "Spotify User");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "spotify-id-123"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("spotify@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User savedUser = User.builder().idUser(4L).username("spotifyu").email("spotify@test.com").build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(4L)).thenReturn(List.of());

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loadUser_whenNameHasMultipleParts_shouldSplitFirstAndLastName() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "test-id");
        attrs.put("email", "test@test.com");
        attrs.put("name", "John Doe Smith");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "test-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User savedUser = User.builder().idUser(5L).username("johns").email("test@test.com").build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(5L)).thenReturn(List.of());

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isNotNull();
    }

    @Test
    void loadUser_whenOAuth2ExceptionThrown_shouldRethrow() {
        when(restOperations.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));


        assertThatThrownBy(() -> service.loadUser(buildUserRequest("google")))
                .isInstanceOfSatisfying(OAuth2AuthenticationException.class,
                        ex -> assertThat(ex.getError().getErrorCode())
                                .isEqualTo("invalid_user_info_response"));
    }

    @Test
    void loadUser_whenRepositoryThrowsException_shouldWrapAsOAuth2Exception() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "test-id");
        attrs.put("email", "test@test.com");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId(anyString(), anyString()))
                .thenThrow(new RuntimeException("DB connection failed"));

        assertThatThrownBy(() -> service.loadUser(buildUserRequest("google")))
                .isInstanceOfSatisfying(OAuth2AuthenticationException.class, ex ->
                    assertThat(ex.getError().getErrorCode()).isEqualTo("Internal error during OAuth2 processing")
                );
    }

    @Test
    void loadUser_whenNameIsSingleWord_shouldUseNameAsUsernameWithEmptyLastName() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "single-word-id");
        attrs.put("email", "singleword@test.com");
        attrs.put("name", "Mono");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "single-word-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("singleword@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User savedUser = User.builder().idUser(7L).username("mono").email("singleword@test.com").build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(7L)).thenReturn(List.of());

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isNotNull();
        verify(userRepository).save(argThat(u -> u.getLastName() != null));
    }

    @Test
    void loadUser_whenBothFirstNameAndNameAreNull_shouldUseDefaultUsername() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "null-name-id");
        attrs.put("email", "nullname@test.com");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "null-name-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("nullname@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User savedUser = User.builder().idUser(8L).username("user").email("nullname@test.com").build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(8L)).thenReturn(List.of());

        OAuth2User result = service.loadUser(buildUserRequest("google"));

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loadUser_whenUsernameLongerThan16Chars_shouldTruncate() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("sub", "long-name-id");
        attrs.put("email", "longname@test.com");
        attrs.put("given_name", "Verylongnamehere");
        attrs.put("family_name", "XYZ");
        mockRestOperationsResponse(attrs);

        when(oAuthAccountRepository.findByProviderAndProviderUserId("google", "long-name-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("longname@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User savedUser = User.builder().idUser(6L).username("verylongnameher").email("longname@test.com").build();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertThat(u.getUsername().length()).isLessThanOrEqualTo(16);
            return savedUser;
        });
        when(oAuthAccountRepository.save(any())).thenReturn(new OAuthAccount());
        when(roleRepository.findAllByUserId(6L)).thenReturn(List.of());

        service.loadUser(buildUserRequest("google"));

        verify(userRepository).save(any(User.class));
    }
}
