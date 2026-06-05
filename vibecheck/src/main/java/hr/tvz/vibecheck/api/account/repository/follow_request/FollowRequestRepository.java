package hr.tvz.vibecheck.api.account.repository.follow_request;

import hr.tvz.vibecheck.api.account.dto.FollowRequestResponse;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.quartz.DTO.PendingFollowRequestReminderTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    Optional<FollowRequest> findBySender_IdUserAndReceiver_IdUser(Long senderId, Long receiverId);

    @Query("""
    SELECT new hr.tvz.vibecheck.api.account.dto.FollowRequestResponse(
        fr.idRequest,
        fr.sender.idUser,
        fr.sender.firstName,
        fr.sender.lastName,
        fr.sender.avatarUrl,
        fr.createdAt
    )
    FROM FollowRequest fr
    WHERE fr.receiver.idUser = :receiverId AND
          fr.status != 'DECLINED'
""")
    List<FollowRequestResponse> findAllByReceiverId(Long receiverId);

    @Query("""
    SELECT new hr.tvz.vibecheck.quartz.DTO.PendingFollowRequestReminderTarget(
        fr.receiver.idUser,
        fr.receiver.username,
        fr.receiver.email,
        COUNT(fr)
    )
    FROM FollowRequest fr
    WHERE fr.status = 'PENDING'
    GROUP BY fr.receiver.idUser, fr.receiver.username, fr.receiver.email
    HAVING COUNT(fr) >= :minPendingCount
""")
    List<PendingFollowRequestReminderTarget> findPendingFollowRequestReminderTargets(int minPendingCount);

}
