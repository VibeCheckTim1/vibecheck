package hr.tvz.vibecheck.security.jwt;

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
public class JwtService {

    private static final String TYPE = "type";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access}")
    private long accessExpirationMinutes;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpirationDays;

    public String generateAccessToken(VibeCheckUserDetails user) {
        Instant expiration = Instant.now().plus(accessExpirationMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim(TYPE, TokenType.ACCESS)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    public String generateRefreshToken(VibeCheckUserDetails user) {
        Instant expiration = Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS);

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim(TYPE, TokenType.REFRESH)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    public Cookie generateAccessTokenCookie(String token) {
        var cookie = new Cookie(TokenType.ACCESS, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false); // set to true in production with HTTPS
        cookie.setMaxAge(-1);

        return cookie;
    }

    public Cookie generateRefreshTokenCookie(String token) {
        var cookie = new Cookie(TokenType.REFRESH, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false); // set to true in production with HTTPS
        cookie.setMaxAge((int) (refreshExpirationDays * 24 * 60 * 60));

        return cookie;
    }

    public boolean isValid(String token, String type) {
        var decrypted = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);

        return decrypted.getClaim(TYPE).asString().equals(type);
    }

    public String extractTokenFromCookie(HttpServletRequest request, String type) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (type.equals(cookie.getName())) {
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
