package hr.tvz.vibecheck.exception.framework;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void record_shouldStoreAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 6, 15, 10, 30);

        ErrorResponse response = new ErrorResponse(
                "session-id-123",
                "/api/resource",
                404,
                "Resource not found",
                timestamp
        );

        assertThat(response.sid()).isEqualTo("session-id-123");
        assertThat(response.uri()).isEqualTo("/api/resource");
        assertThat(response.status()).isEqualTo(404);
        assertThat(response.message()).isEqualTo("Resource not found");
        assertThat(response.timestamp()).isEqualTo(timestamp);
    }

    @Test
    void record_equality_shouldBeValueBased() {
        LocalDateTime ts = LocalDateTime.of(2024, 1, 1, 0, 0);

        ErrorResponse r1 = new ErrorResponse("sid", "/uri", 400, "Bad", ts);
        ErrorResponse r2 = new ErrorResponse("sid", "/uri", 400, "Bad", ts);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).hasSameHashCodeAs(r2.hashCode());
    }

    @Test
    void record_toString_shouldContainFields() {
        LocalDateTime ts = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse("sid-abc", "/test", 500, "Error", ts);

        String str = response.toString();

        assertThat(str).contains("sid-abc").contains("/test").contains("500");
    }
}
