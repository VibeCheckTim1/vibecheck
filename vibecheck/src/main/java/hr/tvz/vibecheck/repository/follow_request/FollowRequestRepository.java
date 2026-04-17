package hr.tvz.vibecheck.repository.follow_request;

import hr.tvz.vibecheck.dto.response.FollowRequestResponse;
import hr.tvz.vibecheck.entity.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    Optional<FollowRequest> findBySender_IdUserAndReceiver_IdUser(Long senderId, Long receiverId);

    @Query("""
    SELECT new hr.tvz.vibecheck.dto.response.FollowRequestResponse(
        fr.idRequest,
        fr.sender.idUser,
        fr.sender.firstName,
        fr.sender.lastName,
        fr.createdAt
    )
    FROM FollowRequest fr
    WHERE fr.receiver.idUser = :receiverId
""")
    List<FollowRequestResponse> findAllByReceiverId(Long receiverId);

}
