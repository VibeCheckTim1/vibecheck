package hr.tvz.vibecheck.api.account.service.interfaces;

import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.api.user.entity.User;

public interface FollowRequestActionHandler {
    FollowRequestUserResponse action();
    void handle(FollowRequest followRequest, User receiver);
}
