package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.dto.request.ChangePasswordRequest;
import hr.tvz.vibecheck.dto.request.CreateUserRequest;
import hr.tvz.vibecheck.dto.request.EditUserRequest;
import hr.tvz.vibecheck.dto.request.LoginRequest;
import hr.tvz.vibecheck.dto.response.UserEditResponse;
import hr.tvz.vibecheck.dto.response.UserResponse;
import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.projections.UserStateResponse;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import hr.tvz.vibecheck.service.StateService;
import hr.tvz.vibecheck.service.security.AuthService;
import hr.tvz.vibecheck.service.security.JwtService;
import hr.tvz.vibecheck.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final StateService stateService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<UserStateResponse> createUser(@RequestBody @Valid CreateUserRequest request, HttpServletResponse response) {

        userService.createUser(request);

        var loginRequest = new LoginRequest(request.username(), request.password());
        authService.login(loginRequest, response);

        var userState = stateService.getUserState();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PostMapping(value = "/addAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserStateResponse> uploadAvatar(
            @RequestParam("file") MultipartFile avatar, @AuthenticationPrincipal VibeCheckUserDetails userDetails
            ) throws IOException {

        Long userId = userDetails.getId();

        userService.uploadAvatar(userId, avatar);

        var userState = stateService.getUserState();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PatchMapping("/edit/{userId}")
    public ResponseEntity<UserEditResponse> edit(@PathVariable Long userId, @RequestBody @Valid EditUserRequest request) {
        return ResponseEntity.ok(userService.editUser(userId, request));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> delete(@PathVariable Long userId, HttpServletResponse response) {
        userService.deleteUser(userId);

        var accessCookieToken = jwtService.generateCookieToken(TokenType.ACCESS, null, true);
        var refreshCookieToken = jwtService.generateCookieToken(TokenType.REFRESH, null, true);

        response.addCookie(accessCookieToken);
        response.addCookie(refreshCookieToken);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                            @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        userService.changePassword(userDetails.getId(), request);

        return ResponseEntity.noContent().build();
    }

}
