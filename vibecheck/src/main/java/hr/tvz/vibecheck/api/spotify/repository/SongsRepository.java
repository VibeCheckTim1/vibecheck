package hr.tvz.vibecheck.api.spotify.repository;

import hr.tvz.vibecheck.api.spotify.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongsRepository extends JpaRepository<Song, Long> {
    Optional<Song> findBySpotifyTrackId(String spotifyTrackId);
    boolean existsBySpotifyTrackId(String spotifyTrackId);
}
