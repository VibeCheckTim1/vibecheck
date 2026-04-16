package hr.tvz.vibecheck.controller.follow_request;

import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.dto.response.FollowActionResponse;
import hr.tvz.vibecheck.service.follow_request.FollowRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/followRequest")
public class FollowRequestController {
    private final FollowRequestService followRequestService;

    @PostMapping
    public ResponseEntity<FollowActionResponse> createFriendRequestOrFollow(@Valid @RequestBody FollowRequestRequest request) {
        return ResponseEntity.ok(followRequestService.createFollowRequestOrFollow(request));
    }

    @DeleteMapping
    public ResponseEntity<?> cancelFollowRequest(@Valid @RequestBody FollowRequestRequest request) {
        followRequestService.cancelFollowRequest(request);
        return ResponseEntity.noContent().build();
    }

}
