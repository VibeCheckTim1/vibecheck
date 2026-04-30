package hr.tvz.vibecheck.api.security.repository;

import hr.tvz.vibecheck.api.security.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

    @Query("""
        SELECT DISTINCT e
        FROM Endpoint e
        LEFT JOIN FETCH e.roles
    """)
    List<Endpoint> findAllWithRoles();
}
