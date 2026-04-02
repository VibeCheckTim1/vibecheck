package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.service.StateService;
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

    private final StateService stateService;

    @GetMapping("/user-state")
    public ResponseEntity<?> getUserState() {

        var userState = stateService.getUserState();

        if (userState == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        else return ResponseEntity.ok((userState));
    }
}
