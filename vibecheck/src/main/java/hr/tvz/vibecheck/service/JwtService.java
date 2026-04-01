package hr.tvz.vibecheck.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access}")
    private long accessExpirationMinutes;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpirationDays;

    public String generateAccessToken(Authentication auth, String token) {

        var accessExpiration = LocalDateTime.now().plusMinutes(accessExpirationMinutes);

        return generate(auth, accessExpiration, TokenType.ACCESS, token);
    }

    public String generateRefreshToken(Authentication auth) {

        var refreshExpiration = LocalDateTime.now().plusDays(refreshExpirationDays);

        return generate(auth, refreshExpiration, TokenType.REFRESH, null);
    }

    private String generate(Authentication auth, LocalDateTime expiration, String type, String token) {
        VibeCheckUserDetails userDetails;
        String email;

        if (auth != null) {

            userDetails = (VibeCheckUserDetails) auth.getPrincipal();

            if (userDetails == null) return null; // TODO: throw error

            email = userDetails.getEmail();

        } else {

            email = extractEmail(token);
        }

        if (type.equals(TokenType.ACCESS) && !isValid(token, TokenType.REFRESH)) return null;  // TODO: throw error

        return JWT.create()
                .withSubject(email)
                .withClaim("type", type)
                .withIssuedAt(new Date())
                .withExpiresAt(expiration.toInstant(ZoneOffset.UTC))
                .sign(Algorithm.HMAC256(secret));
    }

    public boolean isValid(String token, String type) {
        var decrypted = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);

        return decrypted.getClaim("type").asString().equals(type);
    }

    public @NonNull String extractToken(String header) {
        return header.substring(7);
    }

    public String extractEmail(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getSubject();
    }
}
