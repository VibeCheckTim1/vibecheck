package hr.tvz.vibecheck.api.security.repository;

import hr.tvz.vibecheck.api.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("""
        SELECT r
        FROM User u
        JOIN u.roles r
        WHERE u.idUser = :userId
    """)
    List<Role> findAllByUserId(Long userId);
}
