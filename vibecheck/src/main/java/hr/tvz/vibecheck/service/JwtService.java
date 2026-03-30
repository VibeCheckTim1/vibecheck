package hr.tvz.vibecheck.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
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

    public String generateAccessToken(Authentication auth) {

        var accessExpiration = LocalDateTime.now().plusMinutes(accessExpirationMinutes);

        return generate(auth, accessExpiration);
    }

    public String generateRefreshToken(Authentication auth) {

        var refreshExpiration = LocalDateTime.now().plusDays(refreshExpirationDays);

        return generate(auth, refreshExpiration);
    }

    private String generate(Authentication auth, LocalDateTime expiration) {

        var userDetails = (VibeCheckUserDetails) auth.getPrincipal();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(expiration.toInstant(ZoneOffset.UTC))
                .sign(Algorithm.HMAC256(secret));
    }

    public boolean isValid(String token) {
        var decoded = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return true;
    }
}
