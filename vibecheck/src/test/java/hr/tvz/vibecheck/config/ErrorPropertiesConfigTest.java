package hr.tvz.vibecheck.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ErrorPropertiesConfig.class)
class ErrorPropertiesConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void errorsPropertiesAreLoaded() {
        assertThat(environment.getProperty("bad-request.code")).isEqualTo("400");
        assertThat(environment.getProperty("not-found.code")).isEqualTo("404");
        assertThat(environment.getProperty("conflict.code")).isEqualTo("409");
        assertThat(environment.getProperty("internal-server-error.code")).isEqualTo("500");
        assertThat(environment.getProperty("unauthorized.code")).isEqualTo("401");
        assertThat(environment.getProperty("forbidden.code")).isEqualTo("403");
        assertThat(environment.getProperty("bad-request.message")).isNotBlank();
        assertThat(environment.getProperty("forbidden.message")).isNotBlank();
    }
}
