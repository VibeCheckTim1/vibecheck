package hr.tvz.vibecheck.repository.follow_request;

import hr.tvz.vibecheck.entity.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    Optional<FollowRequest> findBySender_IdUserAndReceiver_IdUser(Long senderId, Long receiverId);
}
