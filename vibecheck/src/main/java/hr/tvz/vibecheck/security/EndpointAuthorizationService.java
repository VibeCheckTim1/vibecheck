package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.api.security.entity.Role;
import hr.tvz.vibecheck.api.security.repository.EndpointRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@DependsOn("liquibase")
@RequiredArgsConstructor
public class EndpointAuthorizationService {

    private final EndpointRepository endpointRepository;

    @Getter
    private List<EndpointAccessRule> endpointAccessRules = List.of();

    @PostConstruct
    @Transactional(readOnly = true)
    public void loadEndpointAccessRules() {
        endpointAccessRules = endpointRepository.findAllWithRoles().stream()
                .map(endpoint -> new EndpointAccessRule(
                        parseHttpMethod(endpoint.getHttpMethod(), endpoint.getPath()),
                        endpoint.getPath(),
                        endpoint.getRoles().stream()
                                .map(Role::getName)
                                .distinct()
                                .toList()
                ))
                .toList();

        log.info("Loaded {} endpoint authorization rules from database", endpointAccessRules.size());
    }

    private HttpMethod parseHttpMethod(String httpMethod, String path) {
        try {
            return HttpMethod.valueOf(httpMethod.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalStateException("Invalid HTTP method '%s' for endpoint '%s'".formatted(httpMethod, path), e);
        }
    }

    public record EndpointAccessRule(
            HttpMethod method,
            String path,
            List<String> roles
    ) {
    }
}
