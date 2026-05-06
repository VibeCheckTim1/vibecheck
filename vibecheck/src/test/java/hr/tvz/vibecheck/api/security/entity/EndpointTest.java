package hr.tvz.vibecheck.api.security.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EndpointTest {

    @Test
    void builder_shouldCreateEndpointWithAllFields() {
        Role role = Role.builder().idRole(1L).name("USER").build();

        Endpoint endpoint = Endpoint.builder()
                .idEndpoint(1L)
                .httpMethod("GET")
                .path("/api/test")
                .description("Test endpoint")
                .roles(List.of(role))
                .build();

        assertThat(endpoint.getIdEndpoint()).isEqualTo(1L);
        assertThat(endpoint.getHttpMethod()).isEqualTo("GET");
        assertThat(endpoint.getPath()).isEqualTo("/api/test");
        assertThat(endpoint.getDescription()).isEqualTo("Test endpoint");
        assertThat(endpoint.getRoles()).containsExactly(role);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyEndpoint() {
        Endpoint endpoint = new Endpoint();

        assertThat(endpoint.getIdEndpoint()).isNull();
        assertThat(endpoint.getHttpMethod()).isNull();
        assertThat(endpoint.getPath()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateEndpoint() {
        Role role = Role.builder().name("ADMIN").build();

        Endpoint endpoint = new Endpoint(2L, "POST", "/api/create", "Create endpoint", List.of(role));

        assertThat(endpoint.getIdEndpoint()).isEqualTo(2L);
        assertThat(endpoint.getHttpMethod()).isEqualTo("POST");
        assertThat(endpoint.getPath()).isEqualTo("/api/create");
        assertThat(endpoint.getDescription()).isEqualTo("Create endpoint");
        assertThat(endpoint.getRoles()).hasSize(1);
    }

    @Test
    void setters_shouldUpdateFields() {
        Endpoint endpoint = new Endpoint();
        endpoint.setIdEndpoint(5L);
        endpoint.setHttpMethod("DELETE");
        endpoint.setPath("/api/delete");
        endpoint.setDescription("Delete endpoint");
        endpoint.setRoles(List.of());

        assertThat(endpoint.getIdEndpoint()).isEqualTo(5L);
        assertThat(endpoint.getHttpMethod()).isEqualTo("DELETE");
        assertThat(endpoint.getPath()).isEqualTo("/api/delete");
        assertThat(endpoint.getDescription()).isEqualTo("Delete endpoint");
        assertThat(endpoint.getRoles()).isEmpty();
    }
}
