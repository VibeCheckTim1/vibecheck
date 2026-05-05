package hr.tvz.vibecheck.api.user.entity;

import hr.tvz.vibecheck.api.security.entity.OAuthAccount;
import hr.tvz.vibecheck.api.security.entity.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void builder_shouldCreateUserWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Role role = Role.builder().name("USER").build();
        OAuthAccount oauthAccount = OAuthAccount.builder().provider("google").build();

        User user = User.builder()
                .idUser(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .avatarUrl("http://example.com/avatar.jpg")
                .avatarPublicId("public-id-123")
                .bio("Hello world")
                .isPrivate(false)
                .email("john@example.com")
                .password("encoded-pass")
                .tstamp(now)
                .oauthAccounts(List.of(oauthAccount))
                .roles(List.of(role))
                .build();

        assertThat(user.getIdUser()).isEqualTo(1L);
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getUsername()).isEqualTo("johndoe");
        assertThat(user.getAvatarUrl()).isEqualTo("http://example.com/avatar.jpg");
        assertThat(user.getAvatarPublicId()).isEqualTo("public-id-123");
        assertThat(user.getBio()).isEqualTo("Hello world");
        assertThat(user.isPrivate()).isFalse();
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded-pass");
        assertThat(user.getTstamp()).isEqualTo(now);
        assertThat(user.getOauthAccounts()).hasSize(1);
        assertThat(user.getRoles()).hasSize(1);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyUser() {
        User user = new User();

        assertThat(user.getIdUser()).isNull();
        assertThat(user.getUsername()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateUser() {
        LocalDateTime ts = LocalDateTime.now();
        User user = new User(2L, "Jane", "Smith", "jsmith", "http://avatar.url", null,
                "bio", true, "jane@example.com", "pass", ts, List.of(), List.of());

        assertThat(user.getIdUser()).isEqualTo(2L);
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getUsername()).isEqualTo("jsmith");
        assertThat(user.isPrivate()).isTrue();
    }

    @Test
    void setters_shouldUpdateFields() {
        User user = new User();
        LocalDateTime ts = LocalDateTime.now();

        user.setIdUser(10L);
        user.setFirstName("Updated");
        user.setLastName("Name");
        user.setUsername("updateduser");
        user.setAvatarUrl("http://new-avatar.com");
        user.setAvatarPublicId("new-public-id");
        user.setBio("Updated bio");
        user.setPrivate(true);
        user.setEmail("updated@example.com");
        user.setPassword("new-pass");
        user.setTstamp(ts);
        user.setOauthAccounts(List.of());
        user.setRoles(List.of());

        assertThat(user.getIdUser()).isEqualTo(10L);
        assertThat(user.getFirstName()).isEqualTo("Updated");
        assertThat(user.getUsername()).isEqualTo("updateduser");
        assertThat(user.isPrivate()).isTrue();
        assertThat(user.getTstamp()).isEqualTo(ts);
    }
}
