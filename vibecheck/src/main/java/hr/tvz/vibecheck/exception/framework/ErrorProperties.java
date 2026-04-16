package hr.tvz.vibecheck.exception.framework;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ErrorProperties {

    private final Environment environment;

    public ErrorProperties(Environment environment) {
        this.environment = environment;
    }

    private Integer getCode(ErrorKey key) {
        return Integer.parseInt(Objects.requireNonNull(environment.getProperty(key.propertyKey() + ".code")));
    }

    private String getMessage(ErrorKey key) {
        return environment.getProperty(key.propertyKey() + ".message");
    }

    public ErrorEntry get(ErrorKey key) {
        return new ErrorEntry(getCode(key), getMessage(key));
    }
}
