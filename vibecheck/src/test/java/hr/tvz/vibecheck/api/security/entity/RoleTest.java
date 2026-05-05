package hr.tvz.vibecheck.api.security.entity;

import hr.tvz.vibecheck.api.user.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void builder_shouldCreateRoleWithAllFields() {
        User user = User.builder().username("testuser").build();
        Endpoint endpoint = Endpoint.builder().path("/api/test").build();

        Role role = Role.builder()
                .idRole(1L)
                .name("USER")
                .description("Default user role")
                .users(List.of(user))
                .endpoints(List.of(endpoint))
                .build();

        assertThat(role.getIdRole()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("USER");
        assertThat(role.getDescription()).isEqualTo("Default user role");
        assertThat(role.getUsers()).containsExactly(user);
        assertThat(role.getEndpoints()).containsExactly(endpoint);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyRole() {
        Role role = new Role();

        assertThat(role.getIdRole()).isNull();
        assertThat(role.getName()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateRole() {
        Role role = new Role(2L, "ADMIN", "Admin role", List.of(), List.of());

        assertThat(role.getIdRole()).isEqualTo(2L);
        assertThat(role.getName()).isEqualTo("ADMIN");
        assertThat(role.getDescription()).isEqualTo("Admin role");
    }

    @Test
    void setters_shouldUpdateFields() {
        Role role = new Role();

        role.setIdRole(5L);
        role.setName("MODERATOR");
        role.setDescription("Moderator role");
        role.setUsers(List.of());
        role.setEndpoints(List.of());

        assertThat(role.getIdRole()).isEqualTo(5L);
        assertThat(role.getName()).isEqualTo("MODERATOR");
        assertThat(role.getDescription()).isEqualTo("Moderator role");
        assertThat(role.getUsers()).isEmpty();
        assertThat(role.getEndpoints()).isEmpty();
    }
}
