package hr.tvz.vibecheck.api.account.service.interfaces;

import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.security.dto.FollowRequestActionRequest;
import hr.tvz.vibecheck.api.security.dto.FollowRequestRequest;

public interface FollowRequestCommandService {
    FollowActionResponse createFollowRequestOrFollow(Long senderId, FollowRequestRequest followRequest);
    void cancelFollowRequest(Long senderId, Long receiverId);
    void unfollow(Long senderId, Long receiverId);
    void acceptOrDeclineFollowRequest(Long requestId, Long receiverId, FollowRequestActionRequest userResponse);

}
