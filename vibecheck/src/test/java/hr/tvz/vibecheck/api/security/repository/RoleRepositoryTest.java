package hr.tvz.vibecheck.api.security.repository;

import hr.tvz.vibecheck.api.security.entity.Role;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("findAllByUserId vraća sve role dodijeljene korisniku")
    void findAllByUserId_returnsUserRoles() {
        Role roleAdmin = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        Role roleUser = roleRepository.save(Role.builder().name("ROLE_USER").build());
        User user = userRepository.save(User.builder()
                .firstName("Test")
                .lastName("User")
                .username("testuser")
                .email("test@test.com")
                .password("pass")
                .avatarUrl("avatar.jpg")
                .tstamp(LocalDateTime.now())
                .roles(new ArrayList<>(List.of(roleAdmin, roleUser)))
                .build());
        em.flush();
        em.clear();

        List<Role> roles = roleRepository.findAllByUserId(user.getIdUser());

        assertThat(roles)
                .hasSize(2)
                .extracting(Role::getName)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    @DisplayName("findAllByUserId vraća praznu listu za korisnika bez rola")
    void findAllByUserId_emptyForUserWithoutRoles() {
        User user = userRepository.save(User.builder()
                .firstName("Test")
                .lastName("User")
                .username("testuser2")
                .email("test2@test.com")
                .password("pass")
                .avatarUrl("avatar.jpg")
                .tstamp(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build());
        em.flush();
        em.clear();

        List<Role> roles = roleRepository.findAllByUserId(user.getIdUser());

        assertThat(roles).isEmpty();
    }

    @Test
    @DisplayName("findAllByUserId vraća praznu listu za nepostojeći userId")
    void findAllByUserId_emptyForNonExistentUser() {
        List<Role> roles = roleRepository.findAllByUserId(9999L);

        assertThat(roles).isEmpty();
    }
}