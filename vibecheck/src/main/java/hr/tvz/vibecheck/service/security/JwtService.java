package hr.tvz.vibecheck.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    public Cookie generateTokenCookie(String type, String refreshToken) {

        String token;

        if (TokenType.ACCESS.equals(type)) token = generateAccessToken(refreshToken);
        else if (TokenType.REFRESH.equals(type)) token = generateRefreshToken();
        else return null;

        return generateCookieToken(type, token, false);
    }

    public String generateAccessToken(String token) {

        var accessExpiration = LocalDateTime.now().plusMinutes(accessExpirationMinutes);

        return generateToken(accessExpiration, TokenType.ACCESS, token);
    }

    public String generateRefreshToken() {

        var refreshExpiration = LocalDateTime.now().plusDays(refreshExpirationDays);

        return generateToken(refreshExpiration, TokenType.REFRESH, null);
    }

    private String generateToken(LocalDateTime expiration, String type, String token) {
        VibeCheckUserDetails userDetails;
        String email;
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {

            userDetails = (VibeCheckUserDetails) auth.getPrincipal();

            if (userDetails == null) return null; // TODO: throw error

            email = userDetails.getEmail();

        } else {

            email = extractEmail(token);
        }

        if (type.equals(TokenType.ACCESS) && (token == null || !isValid(token, TokenType.REFRESH))) return null;  // TODO: throw error

        return JWT.create()
                .withSubject(email)
                .withClaim(TYPE, type)
                .withIssuedAt(new Date())
                .withExpiresAt(expiration.toInstant(ZoneOffset.UTC))
                .sign(Algorithm.HMAC256(secret));
    }

    public Cookie generateCookieToken(String type, String token, boolean age0) {
        var cookie = new Cookie(type.equals(TokenType.ACCESS) ? TokenType.ACCESS : TokenType.REFRESH, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false); // set to true in production with HTTPS
        if (age0) cookie.setMaxAge(0);
        else cookie.setMaxAge(type.equals(TokenType.ACCESS)
                ? (-1) // session cookie
                : (int) (refreshExpirationDays * 24 * 60 * 60)); //seconds

        return cookie;
    }

    public boolean isValid(String token, String type) {
        var decrypted = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);

        return decrypted.getClaim(TYPE).asString().equals(type);
    }

    public String getTokenFromCookie(HttpServletRequest request, String type) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (type.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String extractEmail(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getSubject();
    }
}
