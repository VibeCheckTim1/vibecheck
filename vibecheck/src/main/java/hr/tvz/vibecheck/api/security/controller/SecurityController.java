package hr.tvz.vibecheck.api.security.controller;

import hr.tvz.vibecheck.api.security.dto.RegisterRequestDto;
import hr.tvz.vibecheck.api.security.dto.LoginRequestDto;
import hr.tvz.vibecheck.api.security.dto.TokenOutputDto;
import hr.tvz.vibecheck.api.security.enums.TokenType;
import hr.tvz.vibecheck.api.security.service.SecurityService;
import hr.tvz.vibecheck.api.security.projections.UserStateResponse;
import hr.tvz.vibecheck.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityService securityService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserStateResponse> register(@RequestBody @Valid RegisterRequestDto registerRequestDto, HttpServletResponse response) {
        var user = securityService.register(registerRequestDto);

        TokenOutputDto tokenOutputDto = securityService.loginWithUsername(user.username());
        this.populateSecurityResponse(tokenOutputDto, response);

        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenOutputDto tokenOutputDto = securityService.loginWithCredentials(loginRequestDto);
        this.populateSecurityResponse(tokenOutputDto, response);

        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser() {
        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(c -> TokenType.REFRESH.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        if (jwtService.isValid(refreshToken, TokenType.REFRESH)) {
            var username = jwtService.extractUsername(refreshToken);

            TokenOutputDto tokenOutputDto = securityService.loginWithUsername(username);
            this.populateSecurityResponse(tokenOutputDto, response);

            var userState = securityService.getCurrentUser();
            if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            return ResponseEntity.ok(userState);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie access = ResponseCookie.from(TokenType.ACCESS, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refresh = ResponseCookie.from(TokenType.REFRESH, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header("Set-Cookie", access.toString())
                .header("Set-Cookie", refresh.toString())
                .build();
    }

    private void populateSecurityResponse(TokenOutputDto tokenOutputDto, HttpServletResponse response) {
        Cookie accessTokenCookie = jwtService.generateAccessTokenCookie(tokenOutputDto.accessToken());
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = jwtService.generateRefreshTokenCookie(tokenOutputDto.refreshToken());
        response.addCookie(refreshTokenCookie);
    }
}
