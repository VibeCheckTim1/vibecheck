package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.dto.request.CreateUserRequest;
import hr.tvz.vibecheck.dto.request.EditUserRequest;
import hr.tvz.vibecheck.dto.response.UserEditResponse;
import hr.tvz.vibecheck.dto.response.UserResponse;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import hr.tvz.vibecheck.service.user.UserService;
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

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @PostMapping(value = "/addAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile avatar, @AuthenticationPrincipal VibeCheckUserDetails userDetails
            ) throws IOException {

        Long userId = userDetails.getId();

        userService.uploadAvatar(userId, avatar);

        return ResponseEntity.ok().build();
    }

    /*@PutMapping(value = "/edit/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> editUser(
            @PathVariable Long userId,
            @RequestPart("request") EditUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile avatar)
            throws IOException {
        return ResponseEntity.ok(userService.editUser(userId, request, avatar));
    }*/

    /*@PutMapping("avatarEdit/{userId}")
    public*/

    @PutMapping("/edit/{userId}")
    public ResponseEntity<UserEditResponse> edit(@PathVariable Long userId, @RequestBody @Valid EditUserRequest request) {
        return ResponseEntity.ok(userService.editUser(userId, request));
    }

}
