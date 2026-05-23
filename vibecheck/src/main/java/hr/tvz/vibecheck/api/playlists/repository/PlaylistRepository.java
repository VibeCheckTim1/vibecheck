package hr.tvz.vibecheck.api.playlists.repository;

import hr.tvz.vibecheck.api.playlists.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findAllByUser_IdUserOrderByIdDesc(Long userId);

    List<Playlist> findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(Long userId);
}
