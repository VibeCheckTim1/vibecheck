package hr.tvz.vibecheck.exception.custom;

public class EmptySpotifyTrackException extends RuntimeException {
    public EmptySpotifyTrackException() {super("Spotify track is empty!");}
    public EmptySpotifyTrackException(String message) {
        super(message);
    }
}
