package hr.tvz.vibecheck.api.account.service;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.handler.DeclineFollowRequestActionHandler;
import hr.tvz.vibecheck.api.security.enums.FollowRequestStatus;
import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.api.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeclineFollowRequestActionHandlerTest {
    @Test
    void actionReturnsDecline() {
        DeclineFollowRequestActionHandler handler = new DeclineFollowRequestActionHandler();

        assertThat(handler.action()).isEqualTo(FollowRequestUserResponse.DECLINE);
    }

    @Test
    void handleSetsRequestStatusToDeclined() {
        DeclineFollowRequestActionHandler handler = new DeclineFollowRequestActionHandler();

        User sender = User.builder()
                .idUser(1L)
                .username("sender")
                .build();

        User receiver = User.builder()
                .idUser(2L)
                .username("receiver")
                .build();

        FollowRequest followRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .build();

        handler.handle(followRequest, receiver);

        assertThat(followRequest.getStatus()).isEqualTo(FollowRequestStatus.DECLINED);
    }
}
