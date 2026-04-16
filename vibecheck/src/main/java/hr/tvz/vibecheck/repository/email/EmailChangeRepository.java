package hr.tvz.vibecheck.repository.email;

import hr.tvz.vibecheck.entity.EmailChange;
import hr.tvz.vibecheck.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailChangeRepository extends JpaRepository<EmailChange, Long> {
    Optional<EmailChange> findByUser(User user);
}
