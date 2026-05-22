package hr.tvz.vibecheck.api.spotify.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_artist")
    private Long idArtist;

    @Column(name = "spotify_artist_id", nullable = false, unique = true)
    private String spotifyArtistId;

    @Column(nullable = false)
    private String name;
}
