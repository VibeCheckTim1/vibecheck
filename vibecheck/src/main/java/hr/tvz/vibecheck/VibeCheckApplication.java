package hr.tvz.vibecheck;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
public class VibeCheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(VibeCheckApplication.class, args);

		log.info("http://localhost:8080/h2-console?jsessionid=&url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1&user=sa&password=");
		log.info("http://localhost:8080/swagger-ui/index.html");
		log.info("\u001B[32mApplication Started!");
	}
}
