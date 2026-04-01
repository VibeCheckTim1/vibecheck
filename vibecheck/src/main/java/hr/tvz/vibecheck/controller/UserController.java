package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.dto.request.CreateUserRequest;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    //@SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/addAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile avatar/*, @AuthenticationPrincipal VibeCheckUserDetails userDetails*/
            ) throws IOException {

        //Long userId = userDetails.getId();
        Long userId = 2L;

        userService.uploadAvatar(userId, avatar);

        return ResponseEntity.ok().build();
    }

}
