package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.dto.LoginRequest;
import hr.tvz.vibecheck.dto.TokenResponse;
import hr.tvz.vibecheck.service.AuthService;
import hr.tvz.vibecheck.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
        // TODO: validate refresh token, generate new access token
        return ResponseEntity.ok(new TokenResponse("newAccessToken", refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // TODO: invalidate token
        return ResponseEntity.ok().build();
    }
}
