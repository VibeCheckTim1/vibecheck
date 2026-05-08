package hr.tvz.vibecheck.api.account.controller;

import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.entity.Follows;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.api.security.enums.FollowRequestStatus;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:16:///vibecheck",
                "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
                "spring.liquibase.contexts=integration"
        }
)
@AutoConfigureMockMvc
public class FollowRequestApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private FollowsRepository followsRepository;

    @BeforeEach
    void setUp() {
        followRequestRepository.deleteAll();
        followsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /followRequest creates pending follow request through full API flow")
    void shouldCreatePendingFollowRequestThroughFullApiFlow() throws Exception {
        User sender = userRepository.save(createUser("marko", "marko@test.hr", false));
        User receiver = userRepository.save(createUser("đorđ", "dord@test.hr", true));

        mockMvc.perform(post("/followRequest")
                .with(authentication(authenticatedUser(sender.getIdUser())))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "receiverId": %d
                        }
                        """.formatted(receiver.getIdUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PENDING"));


        Optional<FollowRequest> savedRequest =
                followRequestRepository.findBySender_IdUserAndReceiver_IdUser(sender.getIdUser(), receiver.getIdUser());

        assertThat(savedRequest).isPresent();
        assertThat(savedRequest.get().getStatus()).isEqualTo(FollowRequestStatus.PENDING);
        assertThat(savedRequest.get().getSender().getIdUser()).isEqualTo(sender.getIdUser());
        assertThat(savedRequest.get().getReceiver().getIdUser()).isEqualTo(receiver.getIdUser());
    }

    @Test
    @DisplayName("POST /followRequest creates follow relationship when receiver is public")
    void shouldCreateFollowRelationshipWhenReceiverIsPublic() throws Exception {
        User sender = userRepository.save(createUser("marko", "marko@test.hr", false));
        User receiver = userRepository.save(createUser("ana", "ana@test.hr", false));

        mockMvc.perform(post("/followRequest")
                .with(authentication(authenticatedUser(sender.getIdUser())))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "receiverId": %d
                        }
                        """.formatted(receiver.getIdUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FOLLOWING"));


        Optional<Follows> savedFollow =
                followsRepository.findByUser1_IdUserAndUser2_IdUser(sender.getIdUser(), receiver.getIdUser());

        assertThat(savedFollow).isPresent();
        assertThat(savedFollow.get().getUser1().getIdUser()).isEqualTo(sender.getIdUser());
        assertThat(savedFollow.get().getUser2().getIdUser()).isEqualTo(receiver.getIdUser());

        Optional<FollowRequest> savedRequest =
                followRequestRepository.findBySender_IdUserAndReceiver_IdUser(
                        sender.getIdUser(),
                        receiver.getIdUser()
                );

        assertThat(savedRequest).isEmpty();
    }

    private UsernamePasswordAuthenticationToken authenticatedUser(Long userId) {
        VibeCheckUserDetails userDetails = mock(VibeCheckUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
    }

    private User createUser(String username, String email, boolean isPrivate) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAvatarUrl("");
        user.setTstamp(LocalDateTime.now());
        user.setFirstName(username);
        user.setLastName("Test");
        user.setPassword("hashed-password");
        user.setPrivate(isPrivate);
        return user;
    }
}
