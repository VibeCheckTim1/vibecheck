package hr.tvz.vibecheck.exception.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorPropertiesTest {

    @Mock
    private Environment environment;

    private ErrorProperties errorProperties;

    @BeforeEach
    void setUp() {
        errorProperties = new ErrorProperties(environment);
    }

    @Test
    void get_badRequest_shouldReturnEntry() {
        when(environment.getProperty("bad-request.code")).thenReturn("400");
        when(environment.getProperty("bad-request.message")).thenReturn("Bad request.");

        ErrorEntry entry = errorProperties.get(ErrorKey.BAD_REQUEST);

        assertThat(entry.code()).isEqualTo(400);
        assertThat(entry.message()).isEqualTo("Bad request.");
    }

    @Test
    void get_notFound_shouldReturnEntry() {
        when(environment.getProperty("not-found.code")).thenReturn("404");
        when(environment.getProperty("not-found.message")).thenReturn("Resource not found.");

        ErrorEntry entry = errorProperties.get(ErrorKey.NOT_FOUND);

        assertThat(entry.code()).isEqualTo(404);
        assertThat(entry.message()).isEqualTo("Resource not found.");
    }

    @Test
    void get_conflict_shouldReturnEntry() {
        when(environment.getProperty("conflict.code")).thenReturn("409");
        when(environment.getProperty("conflict.message")).thenReturn("Conflict.");

        ErrorEntry entry = errorProperties.get(ErrorKey.CONFLICT);

        assertThat(entry.code()).isEqualTo(409);
        assertThat(entry.message()).isEqualTo("Conflict.");
    }

    @Test
    void get_internalServerError_shouldReturnEntry() {
        when(environment.getProperty("internal-server-error.code")).thenReturn("500");
        when(environment.getProperty("internal-server-error.message")).thenReturn("Server error.");

        ErrorEntry entry = errorProperties.get(ErrorKey.INTERNAL_SERVER_ERROR);

        assertThat(entry.code()).isEqualTo(500);
        assertThat(entry.message()).isEqualTo("Server error.");
    }

    @Test
    void get_unauthorized_shouldReturnEntry() {
        when(environment.getProperty("unauthorized.code")).thenReturn("401");
        when(environment.getProperty("unauthorized.message")).thenReturn("Unauthorized.");

        ErrorEntry entry = errorProperties.get(ErrorKey.UNAUTHORIZED);

        assertThat(entry.code()).isEqualTo(401);
        assertThat(entry.message()).isEqualTo("Unauthorized.");
    }

    @Test
    void get_forbidden_shouldReturnEntry() {
        when(environment.getProperty("forbidden.code")).thenReturn("403");
        when(environment.getProperty("forbidden.message")).thenReturn("Forbidden.");

        ErrorEntry entry = errorProperties.get(ErrorKey.FORBIDDEN);

        assertThat(entry.code()).isEqualTo(403);
        assertThat(entry.message()).isEqualTo("Forbidden.");
    }

    @Test
    void get_whenCodePropertyMissing_shouldThrowNullPointerException() {
        when(environment.getProperty("bad-request.code")).thenReturn(null);

        assertThatThrownBy(() -> errorProperties.get(ErrorKey.BAD_REQUEST))
                .isInstanceOf(NullPointerException.class);
    }
}
