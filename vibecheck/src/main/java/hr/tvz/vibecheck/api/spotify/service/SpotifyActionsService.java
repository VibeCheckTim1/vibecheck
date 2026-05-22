package hr.tvz.vibecheck.api.spotify.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.spotify.dto.*;
import hr.tvz.vibecheck.api.spotify.entity.Artist;
import hr.tvz.vibecheck.api.spotify.entity.Song;
import hr.tvz.vibecheck.api.spotify.entity.UserLikedSong;
import hr.tvz.vibecheck.api.spotify.repository.ArtistsRepository;
import hr.tvz.vibecheck.api.spotify.repository.SongsRepository;
import hr.tvz.vibecheck.api.spotify.repository.UserLikedSongsRepository;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.custom.DuplicateUserLikedSongException;
import hr.tvz.vibecheck.exception.custom.EmptySpotifyTrackException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotifyActionsService {
    private final SpotifyAppTokenService spotifyAppTokenService;
    private final RestClient restClient;
    private final UserRepository userRepository;
    private final SongsRepository songsRepository;
    private final ArtistsRepository artistsRepository;
    private final UserLikedSongsRepository likedSongsRepository;
    private final UserLikedSongsRepository userLikedSongsRepository;

    public List<SearchResult> trackSearch(String keyword) {
        String accessToken = spotifyAppTokenService.getAccessToken();

        SpotifySearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/search")
                        .queryParam("q", keyword)
                        .queryParam("type", "track")
                        .queryParam("limit", 5)
                        .queryParam("offset", 0)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(SpotifySearchResponse.class);

        if (response == null || response.tracks() == null || response.tracks().items() == null) {
            return List.of();
        }

        return response.tracks().items().stream()
                .map(this::mapTrackItemToSearchResult)
                .toList();
    }

    public UserLikedSong addSongToLikedSongs(Long userId, String spotifySongId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (likedSongsRepository.existsByUserIdUserAndSongSpotifyTrackId(user.getIdUser(), spotifySongId))
            throw new DuplicateUserLikedSongException();

        Optional<Song> songExistsOpt = songsRepository.findBySpotifyTrackId(spotifySongId);

        Song song = songExistsOpt.orElseGet(() -> createSongFromSpotify(spotifySongId));

        UserLikedSong newLikedSong = UserLikedSong.builder()
                .user(user)
                .song(song)
                .likedAt(LocalDateTime.now())
                .build();

        userLikedSongsRepository.save(newLikedSong);

        return newLikedSong;
    }

    private Song createSongFromSpotify(String spotifySongId) {
        Song song;
        SpotifyTrackItem spotifyTrackItem = getSpotifySongByTrackId(spotifySongId);
        song = mapSpotifyTrackToSong(spotifyTrackItem);

        Set<String> spotifyArtistsIds = spotifyTrackItem.artists().stream()
                .map(SpotifyArtistItem::id)
                .collect(Collectors.toSet());

        List<Artist> existingArtists = artistsRepository.findBySpotifyArtistIdIn(spotifyArtistsIds);

        Set<String> existingArtistsIds =
                existingArtists.stream()
                        .map(Artist::getSpotifyArtistId)
                        .collect(Collectors.toSet());

        List<Artist> artistsToSave = spotifyTrackItem.artists().stream()
                .filter(artist -> !existingArtistsIds.contains(artist.id()))
                .map(spotifyArtist -> Artist.builder()
                        .spotifyArtistId(spotifyArtist.id())
                        .name(spotifyArtist.name())
                        .build())
                .toList();

        List<Artist> savedArtists = artistsRepository.saveAll(artistsToSave);

        Set<Artist> allArtists = new HashSet<>();
        allArtists.addAll(existingArtists);
        allArtists.addAll(savedArtists);

        song.setArtists(allArtists);

        return songsRepository.save(song);
    }


    private SpotifyTrackItem getSpotifySongByTrackId(String spotifyTrackId) {
        SpotifyTrackItem response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/tracks/{id}")
                        .build(spotifyTrackId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + spotifyAppTokenService.getAccessToken())
                .retrieve()
                .body(SpotifyTrackItem.class);

        if (response == null) throw new EmptySpotifyTrackException();

        return response;
    }

    private Song mapSpotifyTrackToSong(SpotifyTrackItem track) {
        return Song.builder()
                .spotifyTrackId(track.id())
                .title(track.name())
                .albumName(track.album().name() == null ? null : track.album().name())
                .albumImageUrl(getAlbumImageUrl(track.album()))
                .durationMs(track.durationMs())
                .spotifyUrl(track.externalUrls().spotifyUrl())
                .youtubeUrl(buildYoutubeSearchUrl(track))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String buildYoutubeSearchUrl(SpotifyTrackItem track) {
        String artists = track.artists() == null
                ? ""
                : track.artists().stream()
                .map(SpotifyArtistItem::name)
                .collect(Collectors.joining(" "));

        String query = artists + track.name();

        return "https://www.youtube.com/results?search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    private SearchResult mapTrackItemToSearchResult(SpotifyTrackItem item) {
        String artists = item.artists().stream()
                .map(SpotifyArtistItem::name)
                .collect(Collectors.joining(", "));

        return new SearchResult(
                item.id(),
                "song",
                item.name(),
                artists,
                getAlbumImageUrl(item.album())
        );
    }

    private String getAlbumImageUrl(SpotifyAlbumItem album) {
        if (album == null || album.images() == null || album.images().isEmpty()) {
            return null;
        }

        return album.images().stream()
                .filter(image -> image.height() != null && image.width() != null)
                .filter(image -> image.height() == 300 && image.width() == 300)
                .map(SpotifyImageItem::url)
                .findFirst()
                .orElse(album.images().getFirst().url());
    }


}
