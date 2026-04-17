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
    public ResponseEntity<FollowActionResponse> createFollowRequestOrFollow(
            @Valid @RequestBody FollowRequestRequest request,
            @AuthenticationPrincipal VibeCheckUserDetails userDetailsService) {
        return ResponseEntity.ok(followRequestService.createFollowRequestOrFollow(userDetailsService.getId(), request));
    }

    @DeleteMapping("{receiverId}")
    public ResponseEntity<?> cancelFollowRequest(
            @PathVariable Long receiverId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        followRequestService.cancelFollowRequest(userDetails.getId(), receiverId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unfollow/{receiverId}")
    public ResponseEntity<?> unfollow(
            @PathVariable Long receiverId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        followRequestService.unfollow(userDetails.getId(), receiverId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getFollowStatus/{receiverId}")
    public ResponseEntity<FollowActionResponse> getFollowStatus(
            @PathVariable Long receiverId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        return ResponseEntity.ok(followRequestService.getFollowStatus(userDetails.getId(), receiverId));
    }

}
