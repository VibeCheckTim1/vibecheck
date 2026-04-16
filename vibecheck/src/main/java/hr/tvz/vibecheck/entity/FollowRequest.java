package hr.tvz.vibecheck.entity;

import hr.tvz.vibecheck.enums.FollowRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "follow_request",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "sender_id", "receiver_id" })
    })
public class FollowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private Long idRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FollowRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
