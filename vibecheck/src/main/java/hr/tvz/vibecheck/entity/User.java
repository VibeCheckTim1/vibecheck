package hr.tvz.vibecheck.entity;

import hr.tvz.vibecheck.enums.ProfileVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "avatar_public_id")
    private String avatarPublicId;

    private String bio;

    @Column(name = "profile_visibility")
    @Enumerated(EnumType.STRING)
    private ProfileVisibility visibility;

    private String email;

    private String password;

    private LocalDateTime tstamp;


}
