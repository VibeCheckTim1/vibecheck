package hr.tvz.vibecheck.api.user.controller;

import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserOutputDto> show(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findOneById(userId));
    }
}
