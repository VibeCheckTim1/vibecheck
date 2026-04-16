package hr.tvz.vibecheck.security.oauth;

import hr.tvz.vibecheck.api.security.repository.OAuthAccountRepository;
import hr.tvz.vibecheck.security.jwt.JwtService;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
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
    private final JwtService jwtService;

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

        if (authentication.getPrincipal() instanceof VibeCheckUserDetails userDetails) {
            var accessToken = jwtService.generateAccessToken(userDetails);
            var refreshToken = jwtService.generateRefreshToken(userDetails);

            Cookie accessTokenCookie = jwtService.generateAccessTokenCookie(accessToken);
            response.addCookie(accessTokenCookie);

            Cookie refreshTokenCookie = jwtService.generateRefreshTokenCookie(refreshToken);
            response.addCookie(refreshTokenCookie);

            getRedirectStrategy().sendRedirect(request, response, "http://127.0.0.1:5173/home");
        }
    }
}
