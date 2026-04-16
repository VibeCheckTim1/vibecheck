package hr.tvz.vibecheck.repository.follows;

import hr.tvz.vibecheck.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follows, Long> {
    Optional<Follows> findByUser1_IdUserAndUser2_IdUser(Long senderId, Long receiverId);
}
