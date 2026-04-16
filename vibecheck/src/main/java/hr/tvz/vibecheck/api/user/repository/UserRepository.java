package hr.tvz.vibecheck.api.user.repository;

import hr.tvz.vibecheck.api.security.projections.UserStateResponse;
import hr.tvz.vibecheck.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<UserStateResponse> findUserStateByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<User> searchUsers(String keyword);
}
