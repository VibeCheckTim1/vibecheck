package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.api.account.dto.spotify.SpotifyTokenResponse;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SpotifyAppTokenService {
    private final RestClient restClient;

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String clientSecret;


    private String accessToken;
    private Instant expiresAt = Instant.EPOCH;

    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt.minusSeconds(60))) {
            refreshAccessToken();
        }
        return accessToken;
    }


    private void refreshAccessToken() {
        String basicAuth = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)
        );

        SpotifyTokenResponse response = restClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials")
                .retrieve()
                .body(SpotifyTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("Spotify token response is empty");
        }

        this.accessToken = response.accessToken();
        this.expiresAt = Instant.now().plusSeconds(response.expiresIn());

    }

}
