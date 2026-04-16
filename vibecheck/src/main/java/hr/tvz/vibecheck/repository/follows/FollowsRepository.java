package hr.tvz.vibecheck.repository.follows;

import hr.tvz.vibecheck.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowsRepository extends JpaRepository<Follows, Long> {
    boolean existsByUser1_IdUserAndUser2_IdUser(Long senderId, Long receiverId);
}
