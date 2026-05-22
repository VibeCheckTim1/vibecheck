package hr.tvz.vibecheck.api.playlists.repository;

import hr.tvz.vibecheck.api.playlists.entity.Playlist;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PlaylistRepositoryTest {

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager em;

    private User saveUser(String username) {
        return userRepository.save(User.builder()
                .firstName("Test")
                .lastName("User")
                .username(username)
                .email(username + "@test.com")
                .password("pass")
                .avatarUrl("avatar.jpg")
                .tstamp(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("findAllByUser_IdUserOrderByIdDesc vraca sve playliste korisnika")
    void findAllByUserId_returnsAllUserPlaylists() {
        User user = saveUser("playlistuser");
        User otherUser = saveUser("otherpluser");
        Playlist first = playlistRepository.save(Playlist.builder()
                .name("First")
                .isFavorite(false)
                .isPublic(true)
                .user(user)
                .build());
        Playlist second = playlistRepository.save(Playlist.builder()
                .name("Second")
                .isFavorite(true)
                .isPublic(false)
                .user(user)
                .build());
        playlistRepository.save(Playlist.builder()
                .name("Other")
                .isFavorite(false)
                .isPublic(true)
                .user(otherUser)
                .build());
        em.flush();
        em.clear();

        List<Playlist> playlists = playlistRepository.findAllByUser_IdUserOrderByIdDesc(user.getIdUser());

        assertThat(playlists)
                .hasSize(2)
                .extracting(Playlist::getId)
                .containsExactly(second.getId(), first.getId());
    }

    @Test
    @DisplayName("findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc vraca samo javne playliste")
    void findAllByUserIdAndIsPublicTrue_returnsOnlyPublicUserPlaylists() {
        User user = saveUser("publicpluser");
        Playlist publicPlaylist = playlistRepository.save(Playlist.builder()
                .name("Public")
                .isFavorite(false)
                .isPublic(true)
                .user(user)
                .build());
        playlistRepository.save(Playlist.builder()
                .name("Private")
                .isFavorite(false)
                .isPublic(false)
                .user(user)
                .build());
        em.flush();
        em.clear();

        List<Playlist> playlists = playlistRepository.findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(user.getIdUser());

        assertThat(playlists)
                .hasSize(1)
                .extracting(Playlist::getId)
                .containsExactly(publicPlaylist.getId());
    }

    @Test
    @DisplayName("repository metode vracaju praznu listu za korisnika bez playlisti")
    void repositoryMethods_emptyForUserWithoutPlaylists() {
        User user = saveUser("emptypluser");

        assertThat(playlistRepository.findAllByUser_IdUserOrderByIdDesc(user.getIdUser())).isEmpty();
        assertThat(playlistRepository.findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(user.getIdUser())).isEmpty();
    }
}
