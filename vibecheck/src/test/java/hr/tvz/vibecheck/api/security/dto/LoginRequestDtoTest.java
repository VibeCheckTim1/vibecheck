package hr.tvz.vibecheck.api.security.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestDtoTest {

    @Test
    void record_shouldStoreAndReturnValues() {
        LoginRequestDto dto = new LoginRequestDto("testuser", "testpass");

        assertThat(dto.username()).isEqualTo("testuser");
        assertThat(dto.password()).isEqualTo("testpass");
    }

    @Test
    void record_equality_shouldBeBasedOnValues() {
        LoginRequestDto dto1 = new LoginRequestDto("user", "pass");
        LoginRequestDto dto2 = new LoginRequestDto("user", "pass");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).hasSameHashCodeAs(dto2.hashCode());
    }

    @Test
    void record_toString_shouldContainFields() {
        LoginRequestDto dto = new LoginRequestDto("myuser", "mypass");

        String str = dto.toString();

        assertThat(str).contains("myuser").contains("mypass");
    }
}
