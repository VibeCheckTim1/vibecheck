package hr.tvz.vibecheck.api.account.service;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.entity.Follows;
import hr.tvz.vibecheck.api.account.handler.AcceptFollowRequestActionHandler;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.exception.custom.DuplicateFollowException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AcceptFollowRequestActionHandlerTest {
    @Test
    void actionReturnsAccept() {
        FollowRequestRepository followRequestRepository = mock(FollowRequestRepository.class);
        FollowsRepository followsRepository = mock(FollowsRepository.class);
        AcceptFollowRequestActionHandler handler =
                new AcceptFollowRequestActionHandler(followRequestRepository, followsRepository);

        assert handler.action() == FollowRequestUserResponse.ACCEPT;
    }

    @Test
    void handleDeletesRequestAndCreatesFollowWhenUserIsNotAlreadyFollowing() {
        FollowRequestRepository followRequestRepository = mock(FollowRequestRepository.class);
        FollowsRepository followsRepository = mock(FollowsRepository.class);
        AcceptFollowRequestActionHandler handler =
                new AcceptFollowRequestActionHandler(followRequestRepository, followsRepository);

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
                .build();

        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(1L, 2L))
                .thenReturn(Optional.empty());

        handler.handle(followRequest, receiver);

        verify(followRequestRepository).delete(followRequest);
        verify(followsRepository).save(any(Follows.class));
    }

    @Test
    void handleThrowsWhenUserAlreadyFollowsReceiver() {
        FollowRequestRepository followRequestRepository = mock(FollowRequestRepository.class);
        FollowsRepository followsRepository = mock(FollowsRepository.class);
        AcceptFollowRequestActionHandler handler =
                new AcceptFollowRequestActionHandler(followRequestRepository, followsRepository);

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
                .build();

        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(1L, 2L))
                .thenReturn(Optional.of(Follows.builder().build()));

        assertThatThrownBy(() -> handler.handle(followRequest, receiver))
                .isInstanceOf(DuplicateFollowException.class);

        verify(followRequestRepository, never()).delete(any());
        verify(followsRepository, never()).save(any());
    }
}
