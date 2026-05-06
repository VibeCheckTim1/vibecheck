package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.api.security.entity.Role;
import hr.tvz.vibecheck.api.security.repository.RoleRepository;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VibeCheckUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private VibeCheckUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new VibeCheckUserDetailsService(userRepository, roleRepository);
    }

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        User user = User.builder()
                .idUser(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded-pass")
                .build();
        Role role = Role.builder().name("USER").build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(roleRepository.findAllByUserId(1L)).thenReturn(List.of(role));

        UserDetails result = service.loadUserByUsername("testuser");

        assertThat(result).isInstanceOf(VibeCheckUserDetails.class);
        VibeCheckUserDetails details = (VibeCheckUserDetails) result;
        assertThat(details.getId()).isEqualTo(1L);
        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getEmail()).isEqualTo("test@example.com");
        assertThat(details.getPassword()).isEqualTo("encoded-pass");
        assertThat(details.getRoles()).containsExactly("USER");
    }

    @Test
    void loadUserByUsername_whenUserHasMultipleRoles_shouldReturnAllRoles() {
        User user = User.builder().idUser(2L).username("admin").build();
        Role userRole = Role.builder().name("USER").build();
        Role adminRole = Role.builder().name("ADMIN").build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(roleRepository.findAllByUserId(2L)).thenReturn(List.of(userRole, adminRole));

        UserDetails result = service.loadUserByUsername("admin");

        VibeCheckUserDetails details = (VibeCheckUserDetails) result;
        assertThat(details.getRoles()).containsExactlyInAnyOrder("USER", "ADMIN");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_shouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    void loadUserByUsername_withNoRoles_shouldReturnEmptyRoles() {
        User user = User.builder().idUser(3L).username("noroles").build();

        when(userRepository.findByUsername("noroles")).thenReturn(Optional.of(user));
        when(roleRepository.findAllByUserId(3L)).thenReturn(List.of());

        UserDetails result = service.loadUserByUsername("noroles");

        VibeCheckUserDetails details = (VibeCheckUserDetails) result;
        assertThat(details.getRoles()).isEmpty();
    }
}
