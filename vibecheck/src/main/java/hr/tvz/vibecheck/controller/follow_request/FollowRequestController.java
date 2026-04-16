package hr.tvz.vibecheck.controller.follow_request;

import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.dto.response.FollowActionResponse;
import hr.tvz.vibecheck.enums.FollowActionResult;
import hr.tvz.vibecheck.service.follow_request.FollowRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/followRequest")
public class FollowRequestController {
    private final FollowRequestService followRequestService;

    @PostMapping("/createFollowRequest")
    public ResponseEntity<FollowActionResponse> createFriendRequest(@Valid @RequestBody FollowRequestRequest request) {
        return ResponseEntity.ok(followRequestService.createFollowRequest(request));
    }

}
