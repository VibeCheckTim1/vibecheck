package hr.tvz.vibecheck.api.account.service.interfaces;

import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.account.dto.FollowRequestResponse;
import hr.tvz.vibecheck.api.account.dto.FollowStatsResponse;

import java.util.List;

public interface FollowRequestQueryService {
    FollowActionResponse getFollowStatus(Long senderId, Long receiverId);
    List<FollowRequestResponse> getAllFollowRequests(Long receiverId);
    FollowStatsResponse getFollowStats(Long userId);
}
