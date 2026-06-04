package hr.tvz.vibecheck.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VibeCheckUserDetailsTest {

    @Test
    void builder_shouldCreateUserDetailsWithAllFields() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google-123");

        VibeCheckUserDetails details = VibeCheckUserDetails.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded-pass")
                .roles(List.of("USER", "ADMIN"))
                .attributes(attributes)
                .build();

        assertThat(details.getId()).isEqualTo(1L);
        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getEmail()).isEqualTo("test@example.com");
        assertThat(details.getPassword()).isEqualTo("encoded-pass");
        assertThat(details.getRoles()).containsExactly("USER", "ADMIN");
        assertThat(details.getAttributes()).containsKey("sub");
    }

    @Test
    void getAuthorities_shouldReturnRolesPrefixedWithROLE() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder()
                .username("testuser")
                .roles(List.of("USER", "ADMIN"))
                .build();

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).hasSize(2);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void getAuthorities_withNullRoles_shouldReturnEmptyList() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder()
                .username("testuser")
                .roles(null)
                .build();

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void getName_shouldReturnUsername() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder()
                .username("myuser")
                .build();

        assertThat(details.getName()).isEqualTo("myuser");
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder().username("u").build();
        assertThat(details.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder().username("u").build();
        assertThat(details.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder().username("u").build();
        assertThat(details.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder().username("u").build();
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    void builder_withoutUsername_shouldRejectInvalidDetails() {
        assertThatThrownBy(() -> VibeCheckUserDetails.builder().build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("username");
    }

    @Test
    void allArgsConstructor_shouldCreateDetails() {
        Map<String, Object> attrs = Map.of("key", "value");
        VibeCheckUserDetails details = new VibeCheckUserDetails(
                1L, "user", "user@test.com", "pass", List.of("USER"), attrs
        );

        assertThat(details.getId()).isEqualTo(1L);
        assertThat(details.getUsername()).isEqualTo("user");
        assertThat(details.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void getPassword_withoutPassword_shouldReturnNull() {
        VibeCheckUserDetails details = VibeCheckUserDetails.builder()
                .username("user")
                .build();

        assertThat(details.getPassword()).isNull();
        assertThat(details.getAttributes()).isEmpty();
    }
}
