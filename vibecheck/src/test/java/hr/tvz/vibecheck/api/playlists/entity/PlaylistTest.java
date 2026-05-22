package hr.tvz.vibecheck.api.playlists.entity;

import hr.tvz.vibecheck.api.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistTest {

    @Test
    void builder_shouldCreatePlaylistWithAllFields() {
        User user = User.builder().idUser(1L).username("lsaric").build();

        Playlist playlist = Playlist.builder()
                .id(1L)
                .name("Playlist")
                .isFavorite(true)
                .isPublic(false)
                .user(user)
                .build();

        assertThat(playlist.getId()).isEqualTo(1L);
        assertThat(playlist.getName()).isEqualTo("Playlist");
        assertThat(playlist.isFavorite()).isTrue();
        assertThat(playlist.isPublic()).isFalse();
        assertThat(playlist.getUser()).isEqualTo(user);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyPlaylist() {
        Playlist playlist = new Playlist();

        assertThat(playlist.getId()).isNull();
        assertThat(playlist.getName()).isNull();
        assertThat(playlist.getUser()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreatePlaylist() {
        User user = User.builder().idUser(2L).username("mgradiscaj").build();

        Playlist playlist = new Playlist(2L, "All args playlist", false, true, user);

        assertThat(playlist.getId()).isEqualTo(2L);
        assertThat(playlist.getName()).isEqualTo("All args playlist");
        assertThat(playlist.isFavorite()).isFalse();
        assertThat(playlist.isPublic()).isTrue();
        assertThat(playlist.getUser()).isEqualTo(user);
    }

    @Test
    void setters_shouldUpdateFields() {
        User user = User.builder().idUser(3L).username("kstjepanovic").build();
        Playlist playlist = new Playlist();

        playlist.setId(3L);
        playlist.setName("Updated playlist");
        playlist.setFavorite(true);
        playlist.setPublic(false);
        playlist.setUser(user);

        assertThat(playlist.getId()).isEqualTo(3L);
        assertThat(playlist.getName()).isEqualTo("Updated playlist");
        assertThat(playlist.isFavorite()).isTrue();
        assertThat(playlist.isPublic()).isFalse();
        assertThat(playlist.getUser()).isEqualTo(user);
    }
}
