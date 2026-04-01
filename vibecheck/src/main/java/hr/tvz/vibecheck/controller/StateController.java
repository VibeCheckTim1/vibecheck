package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.repository.UserRepository;
import hr.tvz.vibecheck.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateController {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @GetMapping("/user-state")
    public ResponseEntity<?> getUserState(HttpServletRequest request) {

        var header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            var token = header.substring(7);

            if (jwtService.isValid(token, "access")) {
                var email = jwtService.extractEmail(token);

                return userRepository.findUserStateByEmail(email)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());

            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
