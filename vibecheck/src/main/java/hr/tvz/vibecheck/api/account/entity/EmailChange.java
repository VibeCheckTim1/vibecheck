package hr.tvz.vibecheck.api.account.entity;

import hr.tvz.vibecheck.api.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_change_request")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EmailChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private Long requestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id_user")
    private User user;

    @Column(name = "new_email")
    private String newEmail;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "expiration_time")
    private LocalDateTime expiration;

}
