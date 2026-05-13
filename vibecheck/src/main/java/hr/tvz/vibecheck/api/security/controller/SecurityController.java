package hr.tvz.vibecheck.api.security.controller;

import hr.tvz.vibecheck.api.security.dto.RegisterRequestDto;
import hr.tvz.vibecheck.api.security.dto.LoginRequestDto;
import hr.tvz.vibecheck.api.security.dto.TokenOutputDto;
import hr.tvz.vibecheck.api.security.entity.RefreshToken;
import hr.tvz.vibecheck.api.security.service.RefreshTokenService;
import hr.tvz.vibecheck.api.security.service.SecurityService;
import hr.tvz.vibecheck.api.security.projections.UserStateResponse;
import hr.tvz.vibecheck.api.security.service.AccessTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityService securityService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.cookies.secure:false}")
    private boolean secureCookie;

    @PostMapping("/register")
    public ResponseEntity<UserStateResponse> register(@RequestBody @Valid RegisterRequestDto registerRequestDto, HttpServletRequest request, HttpServletResponse response) {
        var user = securityService.register(registerRequestDto);

        TokenOutputDto tokenOutputDto = securityService.loginWithUsername(request.getRemoteAddr(), user.username());
        this.populateSecurityResponse(tokenOutputDto, response);

        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        TokenOutputDto tokenOutputDto = securityService.loginWithCredentials(request.getRemoteAddr(), loginRequestDto);
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
        String token = Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(c -> c.getName().equals("REFRESH"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (token == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            RefreshToken refreshToken = refreshTokenService.isValid(token);
            TokenOutputDto tokenOutputDto = securityService.loginWithUsername(request.getRemoteAddr(), refreshToken.getUser().getUsername());
            this.populateSecurityResponse(tokenOutputDto, response);

            var userState = securityService.getCurrentUser();
            if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            return ResponseEntity.ok(userState);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/revoke-tokens/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revokeUserTokens(@PathVariable Long userId) {
        refreshTokenService.revokeAllByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie access = ResponseCookie.from("ACCESS", "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refresh = ResponseCookie.from("REFRESH", "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header("Set-Cookie", access.toString())
                .header("Set-Cookie", refresh.toString())
                .build();
    }

    private void populateSecurityResponse(TokenOutputDto tokenOutputDto, HttpServletResponse response) {
        Cookie accessTokenCookie = accessTokenService.generateTokenCookie(tokenOutputDto.accessToken());
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = refreshTokenService.generateTokenCookie(tokenOutputDto.refreshToken());
        response.addCookie(refreshTokenCookie);
    }
}
