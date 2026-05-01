package hr.tvz.vibecheck.api.account.repository.follows;

import hr.tvz.vibecheck.api.account.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follows, Long> {
    Optional<Follows> findByUser1_IdUserAndUser2_IdUser(Long senderId, Long receiverId);

    long countByUser2_IdUser(Long userId);
    long countByUser1_IdUser(Long userId);

}
