package hr.tvz.vibecheck.api.account.handler;

import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.entity.Follows;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.api.account.service.interfaces.FollowRequestActionHandler;
import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.exception.custom.DuplicateFollowException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcceptFollowRequestActionHandler implements FollowRequestActionHandler {
    private final FollowRequestRepository followRequestRepository;
    private final FollowsRepository followsRepository;

    @Override
    public FollowRequestUserResponse action() {
        return FollowRequestUserResponse.ACCEPT;
    }

    @Override
    public void handle(FollowRequest followRequest, User receiver) {
        boolean alreadyFollowing = followsRepository
                .findByUser1_IdUserAndUser2_IdUser(followRequest.getSender().getIdUser(), receiver.getIdUser())
                .isPresent();

        if (alreadyFollowing) {
            throw new DuplicateFollowException("User already follows this profile");
        }

        followRequestRepository.delete(followRequest);

        Follows newFollow = Follows.builder()
                .user1(followRequest.getSender())
                .user2(receiver)
                .build();

        followsRepository.save(newFollow);

    }
}
