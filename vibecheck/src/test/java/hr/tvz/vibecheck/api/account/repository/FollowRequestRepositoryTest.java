package hr.tvz.vibecheck.api.account.repository;

import hr.tvz.vibecheck.api.account.dto.FollowRequestResponse;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.security.enums.FollowRequestStatus;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FollowRequestRepositoryTest {
    @Autowired
    private FollowRequestRepository followRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Finds follow request by sender and receiver id")
    void shouldReturnRequest_WhenRequestExists() {
        User sender = userRepository.save((createUser("marko", "marko@test.hr")));
        User receiver = userRepository.save(createUser("ana", "ana@test.hr"));

        FollowRequest followRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        followRequestRepository.save(followRequest);


        Optional<FollowRequest> result = followRequestRepository
                .findBySender_IdUserAndReceiver_IdUser(sender.getIdUser(), receiver.getIdUser());

        assertThat(result).isPresent();
        assertThat(result.get().getSender().getIdUser()).isEqualTo(sender.getIdUser());
        assertThat(result.get().getReceiver().getIdUser()).isEqualTo(receiver.getIdUser());
        assertThat(result.get().getStatus()).isEqualTo(FollowRequestStatus.PENDING);
    }

    @Test
    @DisplayName("Returns empty when follow request does not exist")
    void shouldReturnEmpty_WhenRequestDoesNotExist() {
        Long senderId = 1L;
        Long receiverId = 2L;

        Optional<FollowRequest> result =
                followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId);

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("Finds only non-declined follow requests for receiver")
    void shouldReturnOnlyNonDeclinedFollowRequestsForReceiver()  {
        User sender1 = userRepository.save(createUser("marko", "marko@test.hr"));
        User sender2 = userRepository.save(createUser("ivan", "ivan@test.hr"));
        User receiver = userRepository.save(createUser("ana", "ana@test.hr"));

        FollowRequest pendingRequest = FollowRequest.builder()
                .sender(sender1)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        FollowRequest declinedRequest = FollowRequest.builder()
                .sender(sender2)
                .receiver(receiver)
                .status(FollowRequestStatus.DECLINED)
                .createdAt(LocalDateTime.now())
                .build();

        followRequestRepository.save(pendingRequest);
        followRequestRepository.save(declinedRequest);


        List<FollowRequestResponse> result =
                followRequestRepository.findAllByReceiverId(receiver.getIdUser());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().idRequest()).isEqualTo(pendingRequest.getIdRequest());
        assertThat(result.getFirst().senderId()).isEqualTo(sender1.getIdUser());
    }



    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAvatarUrl("");
        user.setTstamp(LocalDateTime.now());
        user.setFirstName(username);
        user.setLastName("Test");
        user.setPassword("hashed-password");
        user.setPrivate(false);
        return user;
    }

}
