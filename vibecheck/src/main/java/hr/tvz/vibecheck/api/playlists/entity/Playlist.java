package hr.tvz.vibecheck.api.playlists.entity;

import hr.tvz.vibecheck.api.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_playlist")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_favorite", nullable = false)
    private boolean isFavorite;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
