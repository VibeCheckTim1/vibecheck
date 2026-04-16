package hr.tvz.vibecheck.controller.follow_request;

import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.dto.response.FollowActionResponse;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import hr.tvz.vibecheck.service.follow_request.FollowRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/followRequest")
public class FollowRequestController {
    private final FollowRequestService followRequestService;

    @PostMapping
    public ResponseEntity<FollowActionResponse> createFriendRequestOrFollow(
            @Valid @RequestBody FollowRequestRequest request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetailsService) {
        return ResponseEntity.ok(followRequestService.createFollowRequestOrFollow(userDetailsService.getId(), request));
    }

    @DeleteMapping
    public ResponseEntity<?> cancelFollowRequest(
            @Valid @RequestBody FollowRequestRequest request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        followRequestService.cancelFollowRequest(userDetails.getId(), request);
        return ResponseEntity.noContent().build();
    }

}
