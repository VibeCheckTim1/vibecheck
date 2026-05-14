package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.account.dto.FollowRequestActionRequest;
import hr.tvz.vibecheck.api.account.dto.FollowRequestRequest;
import hr.tvz.vibecheck.api.account.dto.FollowStatsResponse;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.entity.Follows;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.api.security.enums.FollowActionResult;
import hr.tvz.vibecheck.api.security.enums.FollowRequestStatus;
import hr.tvz.vibecheck.api.security.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.custom.DuplicateFollowException;
import hr.tvz.vibecheck.exception.custom.DuplicateFollowRequestException;
import hr.tvz.vibecheck.exception.custom.SelfFollowException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowRequestServiceTest {

    @Mock
    private FollowRequestRepository followRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowsRepository followsRepository;

    @InjectMocks
    private FollowRequestService followRequestService;


    @Test
    @DisplayName("Returns FOLLOWING when receiver profile is public")
    void should_ReturnFollow_WhenReceiverIsPublic() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "Marko", false);
        User receiver = createUser(receiverId, "Đorđ", false);

        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId)).thenReturn(Optional.empty());

        FollowActionResponse response = followRequestService.createFollowRequestOrFollow(senderId, request);

        assertThat(response.result()).isEqualTo(FollowActionResult.FOLLOWING);
        verify(followsRepository).save(any(Follows.class));
        verify(followRequestRepository, never()).save(any(FollowRequest.class));
    }

    @Test
    @DisplayName("Returns PENDING when receiver profile is public")
    void should_ReturnPending_WhenReceiverIsPrivate() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "Marko", false);
        User receiver = createUser(receiverId, "Đorđ", true);

        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId)).thenReturn(Optional.empty());
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId)).thenReturn(Optional.empty());

        ArgumentCaptor<FollowRequest> followRequestCaptor = ArgumentCaptor.forClass(FollowRequest.class);

        FollowActionResponse response = followRequestService.createFollowRequestOrFollow(senderId, request);

        assertThat(response.result()).isEqualTo(FollowActionResult.PENDING);

        verify(followRequestRepository).save(followRequestCaptor.capture());
        FollowRequest savedRequest = followRequestCaptor.getValue();

        assertThat(savedRequest.getSender()).isEqualTo(sender);
        assertThat(savedRequest.getReceiver()).isEqualTo(receiver);
        assertThat(savedRequest.getStatus()).isEqualTo(FollowRequestStatus.PENDING);
        assertThat(savedRequest.getCreatedAt()).isNotNull();

        verify(followsRepository, never()).save(any(Follows.class));
    }

    @Test
    @DisplayName("Throws exception when follow request relationship already exists")
    void shouldThrowException_WhenFollowRequestRelationshipAlreadyExists() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "Marko", false);
        User receiver = createUser(receiverId, "Đorđ", true);

        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(new FollowRequest()));

        assertThatThrownBy(() -> followRequestService.createFollowRequestOrFollow(senderId, request))
                .isInstanceOf(DuplicateFollowRequestException.class);

        verify(followsRepository, never()).save(any(Follows.class));
        verify(followRequestRepository, never()).save(any(FollowRequest.class));

    }

    @Test
    @DisplayName("Throws exception when follow relationship already exists")
    void shouldThrowException_WhenFollowRelationshipAlreadyExists() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "Marko", false);
        User receiver = createUser(receiverId, "Đorđ", true);

        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(new Follows()));

        assertThatThrownBy(() -> followRequestService.createFollowRequestOrFollow(senderId, request))
                .isInstanceOf(DuplicateFollowException.class);

        verify(followsRepository, never()).save(any(Follows.class));
        verify(followRequestRepository, never()).save(any(FollowRequest.class));
    }

    @Test
    @DisplayName("Changes declined follow request back to PENDING")
    void shouldReturnPending_WhenDeclinedFollowRequest() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "Marko", false);
        User receiver = createUser(receiverId, "Đorđ", true);

        FollowRequest existingRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.DECLINED)
                .build();

        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId)).thenReturn(Optional.empty());
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(existingRequest));

        FollowActionResponse response = followRequestService.createFollowRequestOrFollow(senderId, request);

        assertThat(existingRequest.getStatus()).isEqualTo(FollowRequestStatus.PENDING);
        assertThat(response.result()).isEqualTo(FollowActionResult.PENDING);
        verify(followRequestRepository).save(existingRequest);
        verify(followsRepository, never()).save(any(Follows.class));
    }

    @Test
    @DisplayName("Throws exception when trying to follow yourself")
    void shouldThrowException_WhenTryingToFollowYourself() {
        Long senderId = 1L;

        User sender = createUser(senderId, "Marko", false);

        FollowRequestRequest request = new FollowRequestRequest(senderId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));

        assertThatThrownBy(() -> followRequestService.createFollowRequestOrFollow(senderId, request))
                .isInstanceOf(SelfFollowException.class);

        verify(followRequestRepository, never()).save(any(FollowRequest.class));
        verify(followsRepository, never()).save(any(Follows.class));
    }

    @Test
    @DisplayName("Throws exception when receiver does not exist")
    void shouldThrowException_WhenReceiverDoesNotExist() {
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = createUser(senderId, "Marko", false);
        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followRequestService.createFollowRequestOrFollow(senderId, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(followsRepository, never()).save(any(Follows.class));
        verify(followRequestRepository, never()).save(any(FollowRequest.class));
    }

    @Test
    @DisplayName("Throws exception when sender does not exist")
    void shouldThrowException_WhenSenderDoesNotExist() {
        Long senderId = 1L;
        Long receiverId = 2L;
        FollowRequestRequest request = new FollowRequestRequest(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followRequestService.createFollowRequestOrFollow(senderId, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(followsRepository, never()).save(any(Follows.class));
        verify(followRequestRepository, never()).save(any(FollowRequest.class));
    }

    @Test
    @DisplayName("Returns FOLLOWING when follow exists")
    void shouldReturnFollowing_WhenFollowExists() {
        Long senderId = 1L;
        Long receiverId = 2L;

        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(new Follows()));

        FollowActionResponse response = followRequestService.getFollowStatus(senderId, receiverId);

        assertThat(response.result()).isEqualTo(FollowActionResult.FOLLOWING);
    }

    @Test
    @DisplayName("Returns PENDING when follow exists")
    void shouldReturnPending_WhenFollowIsRequested() {
        Long senderId = 1L;
        Long receiverId = 2L;

        FollowRequest request = FollowRequest.builder()
                .status(FollowRequestStatus.PENDING)
                .build();

        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(request));

        FollowActionResponse response = followRequestService.getFollowStatus(senderId, receiverId);

        assertThat(response.result()).isEqualTo(FollowActionResult.PENDING);
    }


    @Test
    @DisplayName("Returns FOLLOW when follow exists")
    void shouldReturnDeclined_WhenFollowIsRequested() {
        Long senderId = 1L;
        Long receiverId = 2L;

        FollowRequest request = FollowRequest.builder()
                .status(FollowRequestStatus.DECLINED)
                .build();

        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(request));

        FollowActionResponse response = followRequestService.getFollowStatus(senderId, receiverId);

        assertThat(response.result()).isEqualTo(FollowActionResult.FOLLOW);
    }

    @Test
    @DisplayName("Returns FOLLOW when no follow or request exists")
    void shouldReturnFollow_WhenNothingExists() {
        Long senderId = 1L;
        Long receiverId = 2L;

        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());

        FollowActionResponse response = followRequestService.getFollowStatus(senderId, receiverId);

        assertThat(response.result()).isEqualTo(FollowActionResult.FOLLOW);
    }

    @Test
    @DisplayName("Deletes pending follow request when cancelling")
    void shouldDeletePendingFollowRequestWhenCancelling() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "marko", false);
        User receiver = createUser(receiverId, "ana", true);

        FollowRequest existingRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .build();

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(existingRequest));

        followRequestService.cancelFollowRequest(senderId, receiverId);

        verify(followRequestRepository).delete(existingRequest);
    }

    @Test
    @DisplayName("Deletes follow relationship when unfollowing")
    void shouldDeleteFollowRelationshipWhenUnfollowing() {
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "marko", false);
        User receiver = createUser(receiverId, "ana", false);

        Follows existingFollow = Follows.builder()
                .user1(sender)
                .user2(receiver)
                .build();

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.of(existingFollow));
        when(followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());

        followRequestService.unfollow(senderId, receiverId);

        verify(followsRepository).delete(existingFollow);
    }


    @Test
    @DisplayName("Creates follow when follow request is accepted")
    void shouldCreateFollowWhenFollowRequestIsAccepted() {
        Long requestId = 10L;
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "marko", false);
        User receiver = createUser(receiverId, "ana", true);

        FollowRequest existingRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .build();

        FollowRequestActionRequest userResponse =
                new FollowRequestActionRequest(FollowRequestUserResponse.ACCEPT);

        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
        when(followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId))
                .thenReturn(Optional.empty());

        followRequestService.acceptOrDeclineFollowRequest(requestId, receiverId, userResponse);

        verify(followRequestRepository).delete(existingRequest);
        verify(followsRepository).save(any(Follows.class));
    }


    @Test
    @DisplayName("Changes follow request status to DECLINED when declined")
    void shouldChangeFollowRequestStatusWhenDeclined() {
        Long requestId = 10L;
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = createUser(senderId, "marko", false);
        User receiver = createUser(receiverId, "ana", true);

        FollowRequest existingRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .build();

        FollowRequestActionRequest userResponse =
                new FollowRequestActionRequest(FollowRequestUserResponse.DECLINE);

        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(followRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));

        followRequestService.acceptOrDeclineFollowRequest(requestId, receiverId, userResponse);

        assertThat(existingRequest.getStatus()).isEqualTo(FollowRequestStatus.DECLINED);
        verify(followsRepository, never()).save(any(Follows.class));
        verify(followRequestRepository, never()).delete(any(FollowRequest.class));
    }

    @Test
    @DisplayName("Returns follow stats for existing user")
    void shouldReturnFollowStatsForExistingUser() {
        Long userId = 1L;
        User user = createUser(userId, "marko", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(followsRepository.countByUser2_IdUser(userId)).thenReturn(5L);
        when(followsRepository.countByUser1_IdUser(userId)).thenReturn(3L);

        FollowStatsResponse response = followRequestService.getFollowStats(userId);

        assertThat(response.followersCount()).isEqualTo(5L);
        assertThat(response.followingCount()).isEqualTo(3L);
    }


    private User createUser(Long id, String username, boolean isPrivate) {
        User user = new User();
        user.setIdUser(id);
        user.setUsername(username);
        user.setPrivate(isPrivate);
        return user;
    }

}
