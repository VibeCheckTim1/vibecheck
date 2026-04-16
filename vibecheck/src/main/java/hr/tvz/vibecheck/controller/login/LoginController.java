package hr.tvz.vibecheck.controller.login;

import hr.tvz.vibecheck.dto.request.LoginRequest;
import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.service.state.StateService;
import hr.tvz.vibecheck.service.security.AuthService;
import hr.tvz.vibecheck.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    private final JwtService jwtService;

    private final StateService stateService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        authService.login(loginRequest, response);

        var userState = stateService.getUserState();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PostMapping("/extend-login")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        var refreshToken = jwtService.getTokenFromCookie(request, TokenType.REFRESH);
        if (refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        var accessCookieToken = jwtService.generateTokenCookie(TokenType.ACCESS, refreshToken);

        response.addCookie(accessCookieToken);

        var userState = stateService.getUserState();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        var accessCookieToken = jwtService.generateCookieToken(TokenType.ACCESS, null, true);
        var refreshCookieToken = jwtService.generateCookieToken(TokenType.REFRESH, null, true);

        response.addCookie(accessCookieToken);
        response.addCookie(refreshCookieToken);

        return ResponseEntity.ok().build();
    }
}
