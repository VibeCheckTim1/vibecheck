package hr.tvz.vibecheck.api.account.repository.email_change;

import hr.tvz.vibecheck.api.account.entity.EmailChange;
import hr.tvz.vibecheck.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailChangeRepository extends JpaRepository<EmailChange, Long> {
    Optional<EmailChange> findByUser(User user);
}
