package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.repository.OAuthAccountRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuthAccountRepository oAuthAccountRepository;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            var client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            if (client != null && client.getRefreshToken() != null) {
                var provider = client.getClientRegistration().getRegistrationId();
                var providerUserId = oauthToken.getName();

                oAuthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId).ifPresent(account -> {
                    account.setRefreshToken(client.getRefreshToken().getTokenValue());
                    oAuthAccountRepository.save(account);
                });
            }
        }

        response.addCookie(deleteCookie(TokenType.ACCESS));
        response.addCookie(deleteCookie(TokenType.REFRESH));

        getRedirectStrategy().sendRedirect(request, response, "http://127.0.0.1:5173/home");
    }

    private Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return cookie;
    }
}
