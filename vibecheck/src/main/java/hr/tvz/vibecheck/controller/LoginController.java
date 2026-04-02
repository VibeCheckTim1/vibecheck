package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.dto.LoginRequest;
import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.service.AuthService;
import hr.tvz.vibecheck.service.JwtService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        authService.login(loginRequest, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/extend-login")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        var accessTokenCookie = jwtService.generateTokenCookie(TokenType.ACCESS, jwtService.getTokenFromCookie(request, TokenType.REFRESH));

        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // TODO: invalidate token
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
