package hr.tvz.vibecheck.api.spotify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "songs")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSong;

    @Column(name = "spotify_track_id", nullable = false, unique = true)
    private String spotifyTrackId;

    @Column(nullable = false)
    private String title;

    @Column(name = "album_name")
    private String albumName;

    @Column(name = "album_image_url")
    private String albumImageUrl;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "spotify_url")
    private String spotifyUrl;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "song_artists",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists = new HashSet<>();

}
