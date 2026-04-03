package hr.tvz.vibecheck.service.security;

import hr.tvz.vibecheck.dto.request.LoginRequest;
import hr.tvz.vibecheck.dto.response.TokenResponse;
import hr.tvz.vibecheck.enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public void login(LoginRequest loginRequest, HttpServletResponse response) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        var refreshCookieToken = jwtService.generateTokenCookie(TokenType.REFRESH, null);

        response.addCookie(refreshCookieToken);

        var accessCookieToken = jwtService.generateTokenCookie(TokenType.ACCESS, refreshCookieToken.getValue());

        response.addCookie(accessCookieToken);
    }
}
