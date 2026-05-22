package hr.tvz.vibecheck.api.playlists.service;

import hr.tvz.vibecheck.api.playlists.dto.CreatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.dto.UpdatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.entity.Playlist;
import hr.tvz.vibecheck.api.playlists.repository.PlaylistRepository;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.custom.NotYourRequestException;
import hr.tvz.vibecheck.exception.custom.PlaylistNotFoundException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlaylistService playlistService;

    private User user(Long id, String username) {
        User user = new User();
        user.setIdUser(id);
        user.setUsername(username);
        return user;
    }

    private Playlist playlist(Long id, User user, boolean isPublic) {
        return Playlist.builder()
                .id(id)
                .name("Playlist")
                .isFavorite(false)
                .isPublic(isPublic)
                .user(user)
                .build();
    }

    @Test
    void create_shouldCreatePlaylistForCurrentUser_whenUserExists() {
        User user = user(1L, "lsaric");
        var request = new CreatePlaylistRequestDto("New playlist", true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(playlistRepository.save(any(Playlist.class))).thenAnswer(invocation -> {
            Playlist playlist = invocation.getArgument(0);
            playlist.setId(10L);
            return playlist;
        });

        var result = playlistService.create(1L, request);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.name()).isEqualTo("New playlist");
        assertThat(result.isFavorite()).isTrue();
        assertThat(result.isPublic()).isFalse();
        assertThat(result.userId()).isEqualTo(1L);
        verify(playlistRepository).save(any(Playlist.class));
    }

    @Test
    void create_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.create(99L, new CreatePlaylistRequestDto("Playlist", false, true)))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(playlistRepository);
    }

    @Test
    void findByUserId_shouldReturnAllPlaylists_whenRequesterIsOwner() {
        User owner = user(1L, "lsaric");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(playlistRepository.findAllByUser_IdUserOrderByIdDesc(1L))
                .thenReturn(List.of(playlist(2L, owner, false), playlist(1L, owner, true)));

        var result = playlistService.findByUserId(1L, 1L);

        assertThat(result).hasSize(2);
        verify(playlistRepository).findAllByUser_IdUserOrderByIdDesc(1L);
        verify(playlistRepository, never()).findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(anyLong());
    }

    @Test
    void findByUserId_shouldReturnOnlyPublicPlaylists_whenRequesterIsNotOwner() {
        User owner = user(1L, "lsaric");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(playlistRepository.findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(1L))
                .thenReturn(List.of(playlist(1L, owner, true)));

        var result = playlistService.findByUserId(1L, 2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isPublic()).isTrue();
        verify(playlistRepository).findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(1L);
        verify(playlistRepository, never()).findAllByUser_IdUserOrderByIdDesc(anyLong());
    }

    @Test
    void findOne_shouldReturnPrivatePlaylist_whenRequesterIsOwner() {
        User owner = user(1L, "lsaric");
        when(playlistRepository.findById(2L)).thenReturn(Optional.of(playlist(2L, owner, false)));

        var result = playlistService.findOne(2L, 1L);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.isPublic()).isFalse();
    }

    @Test
    void findOne_shouldReturnPublicPlaylist_whenRequesterIsNotOwner() {
        User owner = user(1L, "lsaric");
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist(1L, owner, true)));

        var result = playlistService.findOne(1L, 2L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.isPublic()).isTrue();
    }

    @Test
    void findOne_shouldThrowNotYourRequestException_whenPlaylistIsPrivateAndRequesterIsNotOwner() {
        User owner = user(1L, "lsaric");
        when(playlistRepository.findById(2L)).thenReturn(Optional.of(playlist(2L, owner, false)));

        assertThatThrownBy(() -> playlistService.findOne(2L, 3L))
                .isInstanceOf(NotYourRequestException.class);
    }

    @Test
    void update_shouldUpdatePlaylist_whenRequesterIsOwner() {
        User owner = user(1L, "lsaric");
        Playlist playlist = playlist(1L, owner, true);
        var request = new UpdatePlaylistRequestDto("Updated playlist", true, false);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        var result = playlistService.update(1L, 1L, request);

        assertThat(result.name()).isEqualTo("Updated playlist");
        assertThat(result.isFavorite()).isTrue();
        assertThat(result.isPublic()).isFalse();
        verify(playlistRepository).save(playlist);
    }

    @Test
    void update_shouldThrowNotYourRequestException_whenRequesterIsNotOwner() {
        User owner = user(1L, "lsaric");
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist(1L, owner, true)));

        assertThatThrownBy(() -> playlistService.update(1L, 2L, new UpdatePlaylistRequestDto("Updated", false, true)))
                .isInstanceOf(NotYourRequestException.class);

        verify(playlistRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeletePlaylist_whenRequesterIsOwner() {
        User owner = user(1L, "lsaric");
        Playlist playlist = playlist(1L, owner, true);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        playlistService.delete(1L, 1L);

        verify(playlistRepository).delete(playlist);
    }

    @Test
    void delete_shouldThrowPlaylistNotFoundException_whenPlaylistDoesNotExist() {
        when(playlistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.delete(99L, 1L))
                .isInstanceOf(PlaylistNotFoundException.class);
    }
}
