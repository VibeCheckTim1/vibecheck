package hr.tvz.vibecheck.api.security.service;

import hr.tvz.vibecheck.api.security.entity.RefreshToken;
import hr.tvz.vibecheck.api.security.enums.TokenType;
import hr.tvz.vibecheck.api.security.repository.RefreshTokenRepository;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.exception.custom.InvalidRefreshTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.expiration.refresh}")
    private long refreshExpirationHours;

    @Value("${app.cookies.secure:false}")
    private boolean secureCookie;

    @Value("${app.cookies.same-site:Lax}")
    private String sameSiteCookie;

    private final RefreshTokenRepository refreshTokenRepository;

    private static final int REFRESH_TOKEN_LENGTH = 64;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateRefreshToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_LENGTH];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public RefreshToken create(User user, String ipAddress) {
        String token = generateRefreshToken();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(refreshExpirationHours);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .ipAddress(ipAddress)
                .createdAt(now)
                .expiresAt(expiration)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Cookie generateTokenCookie(String token) {
        var cookie = new Cookie(TokenType.REFRESH.name(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(secureCookie);
        cookie.setAttribute("SameSite", sameSiteCookie);
        cookie.setMaxAge((int) (refreshExpirationHours * 24 * 60 * 60));

        return cookie;
    }

    @Transactional()
    public RefreshToken isValid(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeAllByUserId(Long userId) {
        List<RefreshToken> tokens =
                refreshTokenRepository.findAllByUser_IdUserAndRevokedFalse(userId);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }
}
