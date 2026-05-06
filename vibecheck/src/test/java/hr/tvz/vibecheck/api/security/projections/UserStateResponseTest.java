package hr.tvz.vibecheck.api.security.projections;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserStateResponseTest {

    private static final class TestUserStateResponse implements UserStateResponse {
        @Override public Long getIdUser() { return 1L; }
        @Override public String getFirstName() { return "John"; }
        @Override public String getLastName() { return "Doe"; }
        @Override public String getUsername() { return "johndoe"; }
        @Override public String getAvatarUrl() { return "http://avatar.url"; }
        @Override public String getBio() { return "Test bio"; }
        @Override public Boolean getIsPrivate() { return false; }
        @Override public String getEmail() { return "john@example.com"; }
        @Override public LocalDateTime getTstamp() { return LocalDateTime.of(2024, 1, 1, 0, 0); }
    }

    @Test
    void getIdUser_shouldReturnId() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getIdUser()).isEqualTo(1L);
    }

    @Test
    void getFirstName_shouldReturnFirstName() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getFirstName()).isEqualTo("John");
    }

    @Test
    void getLastName_shouldReturnLastName() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getUsername_shouldReturnUsername() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void getAvatarUrl_shouldReturnAvatarUrl() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getAvatarUrl()).isEqualTo("http://avatar.url");
    }

    @Test
    void getBio_shouldReturnBio() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getBio()).isEqualTo("Test bio");
    }

    @Test
    void getIsPrivate_shouldReturnPrivacy() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getIsPrivate()).isFalse();
    }

    @Test
    void getEmail_shouldReturnEmail() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getTstamp_shouldReturnTimestamp() {
        UserStateResponse response = new TestUserStateResponse();
        assertThat(response.getTstamp()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
    }
}
