package hr.tvz.vibecheck.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:errors.properties", ignoreResourceNotFound = true)
public class ErrorPropertiesConfig {
}
