package hr.tvz.vibecheck.api.security.repository;

import hr.tvz.vibecheck.api.security.entity.RefreshToken;
import hr.tvz.vibecheck.api.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}