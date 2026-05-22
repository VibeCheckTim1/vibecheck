package hr.tvz.vibecheck.api.spotify.repository;

import hr.tvz.vibecheck.api.spotify.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArtistsRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findBySpotifyArtistId(String spotifyArtistId);

    List<Artist> findBySpotifyArtistIdIn(Set<String> spotifyArtistIds);
}
