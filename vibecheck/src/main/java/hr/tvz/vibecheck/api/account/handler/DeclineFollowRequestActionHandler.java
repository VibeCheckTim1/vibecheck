package hr.tvz.vibecheck.api.account.handler;

import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.service.interfaces.FollowRequestActionHandler;
import hr.tvz.vibecheck.api.security.enums.FollowRequestStatus;
import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.api.user.entity.User;

public class DeclineFollowRequestActionHandler implements FollowRequestActionHandler {
    @Override
    public FollowRequestUserResponse action() {
        return FollowRequestUserResponse.DECLINE;
    }

    @Override
    public void handle(FollowRequest followRequest, User receiver) {
        followRequest.setStatus(FollowRequestStatus.DECLINED);
    }
}
