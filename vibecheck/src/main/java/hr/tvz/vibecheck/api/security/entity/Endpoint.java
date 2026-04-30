package hr.tvz.vibecheck.api.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "endpoints",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"http_method", "path"})
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endpoint")
    private Long idEndpoint;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(nullable = false, length = 255)
    private String path;

    @Column(length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_endpoint",
            joinColumns = @JoinColumn(name = "endpoint_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}
