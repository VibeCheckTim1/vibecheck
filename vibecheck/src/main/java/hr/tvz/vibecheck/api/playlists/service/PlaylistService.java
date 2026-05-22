package hr.tvz.vibecheck.api.playlists.service;

import hr.tvz.vibecheck.api.playlists.dto.CreatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.dto.PlaylistResponseDto;
import hr.tvz.vibecheck.api.playlists.dto.UpdatePlaylistRequestDto;
import hr.tvz.vibecheck.api.playlists.entity.Playlist;
import hr.tvz.vibecheck.api.playlists.repository.PlaylistRepository;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.custom.NotYourRequestException;
import hr.tvz.vibecheck.exception.custom.PlaylistNotFoundException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Transactional
    public PlaylistResponseDto create(Long userId, CreatePlaylistRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Playlist playlist = Playlist.builder()
                .name(request.name())
                .isFavorite(Boolean.TRUE.equals(request.isFavorite()))
                .isPublic(request.isPublic() == null || request.isPublic())
                .user(user)
                .build();

        return toResponse(playlistRepository.save(playlist));
    }

    @Transactional(readOnly = true)
    public List<PlaylistResponseDto> findByUserId(Long userId, Long requesterId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<Playlist> playlists = userId.equals(requesterId)
                ? playlistRepository.findAllByUser_IdUserOrderByIdDesc(userId)
                : playlistRepository.findAllByUser_IdUserAndIsPublicTrueOrderByIdDesc(userId);

        return playlists
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlaylistResponseDto findOne(Long playlistId, Long requesterId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(PlaylistNotFoundException::new);

        if (!canViewPlaylist(playlist, requesterId)) {
            throw new NotYourRequestException("You do not have permission to access this playlist.");
        }

        return toResponse(playlist);
    }

    @Transactional
    public PlaylistResponseDto update(Long playlistId, Long requesterId, UpdatePlaylistRequestDto request) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(PlaylistNotFoundException::new);
        validateOwner(playlist, requesterId);

        playlist.setName(request.name());
        playlist.setFavorite(Boolean.TRUE.equals(request.isFavorite()));
        playlist.setPublic(request.isPublic() == null || request.isPublic());

        return toResponse(playlistRepository.save(playlist));
    }

    @Transactional
    public void delete(Long playlistId, Long requesterId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(PlaylistNotFoundException::new);
        validateOwner(playlist, requesterId);

        playlistRepository.delete(playlist);
    }

    private boolean canViewPlaylist(Playlist playlist, Long requesterId) {
        return playlist.isPublic() || playlist.getUser().getIdUser().equals(requesterId);
    }

    private void validateOwner(Playlist playlist, Long requesterId) {
        if (!playlist.getUser().getIdUser().equals(requesterId)) {
            throw new NotYourRequestException("Only the playlist owner can change this playlist.");
        }
    }

    private PlaylistResponseDto toResponse(Playlist playlist) {
        return new PlaylistResponseDto(
                playlist.getId(),
                playlist.getName(),
                playlist.isFavorite(),
                playlist.isPublic(),
                0,
                playlist.getUser().getIdUser(),
                playlist.getUser().getUsername()
        );
    }
}
