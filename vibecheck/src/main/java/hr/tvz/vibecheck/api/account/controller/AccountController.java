package hr.tvz.vibecheck.api.account.controller;

import hr.tvz.vibecheck.api.account.dto.ChangeEmailRequestDto;
import hr.tvz.vibecheck.api.account.dto.UpdateAccountRequestDto;
import hr.tvz.vibecheck.api.account.dto.VerificationCodeRequestDto;
import hr.tvz.vibecheck.api.account.service.AccountService;
import hr.tvz.vibecheck.api.security.service.SecurityService;
import hr.tvz.vibecheck.api.account.dto.ChangePasswordRequestDto;
import hr.tvz.vibecheck.api.security.projections.UserStateResponse;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private final SecurityService securityService;

    @Value("${app.cookies.secure:false}")
    private boolean secureCookie;

    @PatchMapping("")
    public ResponseEntity<UserStateResponse> updateAccount(
            @RequestBody @Valid UpdateAccountRequestDto request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) {
        accountService.updateAccount(userDetails.getId(), request);

        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete(@AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        accountService.deleteAccount(userDetails.getId());

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

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserStateResponse> uploadAvatar(
            @RequestParam("file") MultipartFile avatar,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) throws IOException {
        accountService.uploadAvatar(userDetails.getId(), avatar);

        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequestDto request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails
    ) {
        accountService.changePassword(userDetails.getId(), request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/emailVerificationCode")
    public ResponseEntity<?> sendEmailVerificationCode(@RequestBody @Valid ChangeEmailRequestDto request,
                                                       @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        accountService.sendEmailVerificationCode(userDetails.getId(), request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/confirmMailEdit")
    public ResponseEntity<UserStateResponse> confirmMailEdit(@RequestBody @Valid VerificationCodeRequestDto request,
                                                             @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        accountService.confirmEmailEdit(userDetails.getId(), request);

        var userState = securityService.getCurrentUser();
        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userState);
    }
}
