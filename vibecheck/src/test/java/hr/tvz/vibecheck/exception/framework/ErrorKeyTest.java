package hr.tvz.vibecheck.exception.framework;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorKeyTest {

    @Test
    void badRequest_shouldHaveCorrectPropertyKey() {
        assertThat(ErrorKey.BAD_REQUEST.propertyKey()).isEqualTo("bad-request");
    }

    @Test
    void notFound_shouldHaveCorrectPropertyKey() {
        assertThat(ErrorKey.NOT_FOUND.propertyKey()).isEqualTo("not-found");
    }

    @Test
    void conflict_shouldHaveCorrectPropertyKey() {
        assertThat(ErrorKey.CONFLICT.propertyKey()).isEqualTo("conflict");
    }

    @Test
    void internalServerError_shouldHaveCorrectPropertyKey() {
        assertThat(ErrorKey.INTERNAL_SERVER_ERROR.propertyKey()).isEqualTo("internal-server-error");
    }

    @Test
    void unauthorized_shouldHaveCorrectPropertyKey() {
        assertThat(ErrorKey.UNAUTHORIZED.propertyKey()).isEqualTo("unauthorized");
    }

    @Test
    void forbidden_shouldHaveCorrectPropertyKey() {
        assertThat(ErrorKey.FORBIDDEN.propertyKey()).isEqualTo("forbidden");
    }

    @Test
    void values_shouldContainAllExpectedKeys() {
        ErrorKey[] values = ErrorKey.values();

        assertThat(values).containsExactlyInAnyOrder(
                ErrorKey.BAD_REQUEST,
                ErrorKey.NOT_FOUND,
                ErrorKey.CONFLICT,
                ErrorKey.INTERNAL_SERVER_ERROR,
                ErrorKey.UNAUTHORIZED,
                ErrorKey.FORBIDDEN
        );
    }

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        assertThat(ErrorKey.valueOf("BAD_REQUEST")).isEqualTo(ErrorKey.BAD_REQUEST);
        assertThat(ErrorKey.valueOf("NOT_FOUND")).isEqualTo(ErrorKey.NOT_FOUND);
        assertThat(ErrorKey.valueOf("CONFLICT")).isEqualTo(ErrorKey.CONFLICT);
        assertThat(ErrorKey.valueOf("INTERNAL_SERVER_ERROR")).isEqualTo(ErrorKey.INTERNAL_SERVER_ERROR);
        assertThat(ErrorKey.valueOf("UNAUTHORIZED")).isEqualTo(ErrorKey.UNAUTHORIZED);
        assertThat(ErrorKey.valueOf("FORBIDDEN")).isEqualTo(ErrorKey.FORBIDDEN);
    }
}
