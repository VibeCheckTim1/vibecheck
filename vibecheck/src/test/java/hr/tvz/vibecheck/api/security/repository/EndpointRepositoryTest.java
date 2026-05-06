package hr.tvz.vibecheck.api.security.repository;

import hr.tvz.vibecheck.api.security.entity.Endpoint;
import hr.tvz.vibecheck.api.security.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EndpointRepositoryTest {

    @Autowired
    EndpointRepository endpointRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("findAllWithRoles vraća sve endpointe s učitanim rolama")
    void findAllWithRoles_returnsEndpointsWithRoles() {
        Role role = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        endpointRepository.save(Endpoint.builder()
                .httpMethod("GET").path("/api/admin")
                .roles(new ArrayList<>(List.of(role))).build());
        endpointRepository.save(Endpoint.builder()
                .httpMethod("GET").path("/api/public")
                .roles(new ArrayList<>()).build());
        em.flush();
        em.clear();

        List<Endpoint> result = endpointRepository.findAllWithRoles();

        assertThat(result).hasSize(2);
        assertThat(result)
                .filteredOn(e -> e.getPath().equals("/api/admin"))
                .first()
                .extracting(e -> e.getRoles().size())
                .isEqualTo(1);
        assertThat(result)
                .filteredOn(e -> e.getPath().equals("/api/public"))
                .first()
                .extracting(e -> e.getRoles().size())
                .isEqualTo(0);
    }

    @Test
    @DisplayName("findAllWithRoles vraća praznu listu kada nema endpointa")
    void findAllWithRoles_emptyWhenNoEndpoints() {
        List<Endpoint> result = endpointRepository.findAllWithRoles();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllWithRoles vraća endpoint bez rola kada nijedna rola nije dodijeljena")
    void findAllWithRoles_endpointWithNoRoles() {
        endpointRepository.save(Endpoint.builder()
                .httpMethod("POST").path("/api/open")
                .roles(new ArrayList<>()).build());
        em.flush();
        em.clear();

        List<Endpoint> result = endpointRepository.findAllWithRoles();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRoles()).isEmpty();
    }
}
