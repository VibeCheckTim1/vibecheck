package hr.tvz.vibecheck.repository;

import hr.tvz.vibecheck.projections.UserStateResponse;
import hr.tvz.vibecheck.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<UserStateResponse> findUserStateByEmail(String email);
}
