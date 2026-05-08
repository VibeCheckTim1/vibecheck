package hr.tvz.vibecheck.api.follow.service;

import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.account.dto.FollowRequestRequest;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.entity.Follows;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.api.account.service.FollowRequestService;
import hr.tvz.vibecheck.api.security.enums.FollowActionResult;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void should_returnFollow_WhenReceiverIsPublic() {
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




    private User createUser(Long id, String username, boolean isPrivate) {
        User user = new User();
        user.setIdUser(id);
        user.setUsername(username);
        user.setPrivate(isPrivate);
        return user;
    }

}
