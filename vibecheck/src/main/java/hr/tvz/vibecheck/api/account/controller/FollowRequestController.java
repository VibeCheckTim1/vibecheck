package hr.tvz.vibecheck.api.account.controller;

import hr.tvz.vibecheck.api.account.dto.FollowStatsResponse;
import hr.tvz.vibecheck.dto.request.FollowRequestActionRequest;
import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.account.dto.FollowRequestResponse;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import hr.tvz.vibecheck.api.account.service.FollowRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PatchMapping("/acceptOrDeclineFollowRequest/{requestId}")
    public ResponseEntity<?> acceptOrDeclineFollowRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal VibeCheckUserDetails userDetails, @RequestBody FollowRequestActionRequest userResponse) {
        followRequestService.acceptOrDeclineFollowRequest(requestId, userDetails.getId(), userResponse);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll")
    public List<FollowRequestResponse> getAllFollowRequests(@AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        return followRequestService.getAllFollowRequests(userDetails.getId());
    }

    @GetMapping("/stats")
    public FollowStatsResponse getFollowStats(@AuthenticationPrincipal VibeCheckUserDetails userDetails) {
        return followRequestService.getFollowStats(userDetails.getId());
    }

}
