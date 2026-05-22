package hr.tvz.vibecheck.api.spotify.repository;

import hr.tvz.vibecheck.api.spotify.entity.Song;
import hr.tvz.vibecheck.api.spotify.entity.UserLikedSong;
import hr.tvz.vibecheck.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLikedSongsRepository extends JpaRepository<UserLikedSong, Long> {
    boolean existsByUserIdUserAndSongSpotifyTrackId(Long userId, String spotifyTrackId);
    Optional<UserLikedSong> findByUserIdUserAndSongIdSong(Long userId, Long songId);
    List<UserLikedSong> findAllByUserIdUserOrderByLikedAtDesc(Long userId);

}
