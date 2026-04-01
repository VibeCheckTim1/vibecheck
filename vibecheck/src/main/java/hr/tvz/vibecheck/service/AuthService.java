package hr.tvz.vibecheck.service;

import hr.tvz.vibecheck.dto.LoginRequest;
import hr.tvz.vibecheck.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public TokenResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var refreshToken = jwtService.generateRefreshToken(auth);
        return new TokenResponse(jwtService.generateAccessToken(null, refreshToken), refreshToken);
    }
}
