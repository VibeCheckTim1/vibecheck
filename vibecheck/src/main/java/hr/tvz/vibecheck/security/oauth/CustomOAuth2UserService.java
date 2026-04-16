package hr.tvz.vibecheck.security.oauth;

import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.api.security.entity.OAuthAccount;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.security.repository.OAuthAccountRepository;
import hr.tvz.vibecheck.api.account.service.MailService;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            var oAuth2User = super.loadUser(userRequest);
            return processOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationException e) {
            log.error("OAuth2 user info loading failed", e);
            throw e;
        } catch (Exception e) {
            log.error("Internal error during OAuth2 user processing", e);
            throw new OAuth2AuthenticationException("Internal error during OAuth2 processing");
        }
    }

    private VibeCheckUserDetails processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        var provider = userRequest.getClientRegistration().getRegistrationId();
        var providerUserId = oAuth2User.getName();

        var oauthAccountOptional = oAuthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);

        User user;
        if (oauthAccountOptional.isPresent()) {
            user = oauthAccountOptional.get().getUser();

            var account = oauthAccountOptional.get();
            updateOAuthAccount(account, userRequest);
            oAuthAccountRepository.save(account);

        } else {
            user = registerNewUser(oAuth2User);

            var newAccount = OAuthAccount.builder()
                    .user(user)
                    .provider(provider)
                    .providerUserId(providerUserId)
                    .build();
            updateOAuthAccount(newAccount, userRequest);
            oAuthAccountRepository.save(newAccount);
        }

        return VibeCheckUserDetails.builder()
                .id(user.getIdUser())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(List.of()) //TODO: kada se dodaju role
                .attributes(oAuth2User.getAttributes())
                .build();
    }

    private User registerNewUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String firstName = oAuth2User.getAttribute("given_name");
        var lastNameAttr = oAuth2User.getAttribute("family_name");
        var lastName = lastNameAttr != null ? lastNameAttr.toString() : "";

        if (firstName == null && name != null) {
            var nameParts = name.split(" ");
            firstName = nameParts[0];
            if (nameParts.length > 1 && lastName.isEmpty()) {
                lastName = nameParts[nameParts.length - 1];
            }
        }

        var usernameBase = firstName != null ? firstName : (name != null ? name : "user");
        if (firstName == null) {
            firstName = name != null ? name : usernameBase;
        }

        if (!lastName.isEmpty()) {
            usernameBase += lastName.charAt(0);
        }
        var username = usernameBase.toLowerCase();
        if (username.length() > 16) username = username.substring(0, 16);
        username = Normalizer.normalize(username, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        username = username.replaceAll("[^a-z0-9]", "");

        var password = generatePassword();
        mailService.sendOAuthPasswordEmail(email, password);

        var encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .avatarUrl("https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg")
                .avatarPublicId(null)
                .isPrivate(true)
                .password(encodedPassword)
                .tstamp(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    private void updateOAuthAccount(OAuthAccount account, OAuth2UserRequest userRequest) {
        account.setAccessToken(userRequest.getAccessToken().getTokenValue());
    }

    private String generatePassword() {
        var characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        var random = new SecureRandom();
        var password = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }
}
