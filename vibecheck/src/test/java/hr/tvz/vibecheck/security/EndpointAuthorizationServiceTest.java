package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.api.security.entity.Endpoint;
import hr.tvz.vibecheck.api.security.entity.Role;
import hr.tvz.vibecheck.api.security.repository.EndpointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndpointAuthorizationServiceTest {

    @Mock
    private EndpointRepository endpointRepository;

    private EndpointAuthorizationService service;

    @BeforeEach
    void setUp() {
        service = new EndpointAuthorizationService(endpointRepository);
    }

    @Test
    void initialState_endpointAccessRules_shouldBeEmptyList() {
        assertThat(service.getEndpointAccessRules()).isEmpty();
    }

    @Test
    void loadEndpointAccessRules_shouldLoadRulesFromRepository() {
        Role userRole = Role.builder().idRole(1L).name("USER").build();
        Role adminRole = Role.builder().idRole(2L).name("ADMIN").build();

        Endpoint endpoint1 = Endpoint.builder()
                .idEndpoint(1L)
                .httpMethod("GET")
                .path("/api/resource")
                .roles(List.of(userRole))
                .build();
        Endpoint endpoint2 = Endpoint.builder()
                .idEndpoint(2L)
                .httpMethod("DELETE")
                .path("/api/admin")
                .roles(List.of(adminRole))
                .build();

        when(endpointRepository.findAllWithRoles()).thenReturn(List.of(endpoint1, endpoint2));

        service.loadEndpointAccessRules();

        List<EndpointAuthorizationService.EndpointAccessRule> rules = service.getEndpointAccessRules();
        assertThat(rules).hasSize(2);
        assertThat(rules.get(0).method()).isEqualTo(HttpMethod.GET);
        assertThat(rules.get(0).path()).isEqualTo("/api/resource");
        assertThat(rules.get(0).roles()).containsExactly("USER");
        assertThat(rules.get(1).method()).isEqualTo(HttpMethod.DELETE);
        assertThat(rules.get(1).path()).isEqualTo("/api/admin");
        assertThat(rules.get(1).roles()).containsExactly("ADMIN");
    }

    @Test
    void loadEndpointAccessRules_withDuplicateRoles_shouldDeduplicateRoles() {
        Role userRole1 = Role.builder().name("USER").build();
        Role userRole2 = Role.builder().name("USER").build();

        Endpoint endpoint = Endpoint.builder()
                .idEndpoint(1L)
                .httpMethod("GET")
                .path("/api/test")
                .roles(List.of(userRole1, userRole2))
                .build();

        when(endpointRepository.findAllWithRoles()).thenReturn(List.of(endpoint));

        service.loadEndpointAccessRules();

        assertThat(service.getEndpointAccessRules().get(0).roles()).containsExactly("USER");
    }

    @Test
    void loadEndpointAccessRules_withEmptyRoles_shouldAllowAnyAuthenticated() {
        Endpoint endpoint = Endpoint.builder()
                .idEndpoint(1L)
                .httpMethod("GET")
                .path("/api/open")
                .roles(List.of())
                .build();

        when(endpointRepository.findAllWithRoles()).thenReturn(List.of(endpoint));

        service.loadEndpointAccessRules();

        assertThat(service.getEndpointAccessRules().get(0).roles()).isEmpty();
    }

    @Test
    void loadEndpointAccessRules_withNoEndpoints_shouldResultInEmptyRules() {
        when(endpointRepository.findAllWithRoles()).thenReturn(List.of());

        service.loadEndpointAccessRules();

        assertThat(service.getEndpointAccessRules()).isEmpty();
    }

    @Test
    void loadEndpointAccessRules_withInvalidHttpMethod_shouldThrowIllegalStateException() {
        Endpoint endpoint = Endpoint.builder()
                .idEndpoint(1L)
                .httpMethod(null)
                .path("/api/test")
                .roles(List.of())
                .build();

        when(endpointRepository.findAllWithRoles()).thenReturn(List.of(endpoint));

        assertThatThrownBy(() -> service.loadEndpointAccessRules())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("/api/test");
    }

    @Test
    void loadEndpointAccessRules_withNullHttpMethod_shouldThrowIllegalStateException() {
        Endpoint endpoint = Endpoint.builder()
                .idEndpoint(1L)
                .httpMethod(null)
                .path("/api/test")
                .roles(List.of())
                .build();

        when(endpointRepository.findAllWithRoles()).thenReturn(List.of(endpoint));

        assertThatThrownBy(() -> service.loadEndpointAccessRules())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void endpointAccessRule_record_shouldStoreAllFields() {
        EndpointAuthorizationService.EndpointAccessRule rule =
                new EndpointAuthorizationService.EndpointAccessRule(
                        HttpMethod.POST, "/api/create", List.of("USER", "ADMIN")
                );

        assertThat(rule.method()).isEqualTo(HttpMethod.POST);
        assertThat(rule.path()).isEqualTo("/api/create");
        assertThat(rule.roles()).containsExactly("USER", "ADMIN");
    }

    @Test
    void loadEndpointAccessRules_shouldSupportAllHttpMethods() {
        List<String> methods = List.of("GET", "POST", "PUT", "PATCH", "DELETE");

        for (String method : methods) {
            Endpoint endpoint = Endpoint.builder()
                    .idEndpoint(1L)
                    .httpMethod(method)
                    .path("/api/test")
                    .roles(List.of())
                    .build();

            when(endpointRepository.findAllWithRoles()).thenReturn(List.of(endpoint));
            service.loadEndpointAccessRules();

            assertThat(service.getEndpointAccessRules()).hasSize(1);
            assertThat(service.getEndpointAccessRules().get(0).method())
                    .isEqualTo(HttpMethod.valueOf(method));
        }
    }
}
