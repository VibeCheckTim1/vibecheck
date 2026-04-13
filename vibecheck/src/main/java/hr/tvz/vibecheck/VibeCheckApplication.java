package hr.tvz.vibecheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class VibeCheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(VibeCheckApplication.class, args);

		log.info("http://127.0.0.1:8080/oauth2/authorization/spotify");
		log.info("http://127.0.0.1:8080/oauth2/authorization/google");
		log.info("http://127.0.0.1:8080/h2-console?jsessionid=&url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1&user=sa&password=");
		log.info("http://127.0.0.1:8080/swagger-ui/index.html");
		log.info("\u001B[32mApplication Started!\u001B[0m");
	}
}
