package hr.tvz.vibecheck.api.account.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenResponse(@JsonProperty("access_token") String accessToken,
                                   @JsonProperty("token_type") String tokenType,
                                   @JsonProperty("expires_in") int expiresIn) {
}
