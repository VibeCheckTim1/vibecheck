package hr.tvz.vibecheck.api.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hr.tvz.vibecheck.api.security.enums.TokenType;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AccessTokenService {

    private static final String TYPE = "type";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access}")
    private long accessExpirationMinutes;

    @Value("${app.cookies.secure:false}")
    private boolean secureCookie;

    public String generateToken(VibeCheckUserDetails user) {
        Instant expiration = Instant.now().plus(accessExpirationMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim(TYPE, TokenType.ACCESS.name())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    public Cookie generateTokenCookie(String token) {
        var cookie = new Cookie(TokenType.ACCESS.name(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(secureCookie);
        cookie.setMaxAge(-1);

        return cookie;
    }

    public boolean isValid(String token) {
        var decrypted = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);

        return decrypted.getClaim(TYPE).asString().equals(TokenType.ACCESS.name());
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(TokenType.ACCESS.name())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getSubject();
    }
}
