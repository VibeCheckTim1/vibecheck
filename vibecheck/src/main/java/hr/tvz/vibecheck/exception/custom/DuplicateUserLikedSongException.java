package hr.tvz.vibecheck.exception.custom;

public class DuplicateUserLikedSongException extends RuntimeException {
    public DuplicateUserLikedSongException() { super("This song was already like by this user"); }
    public DuplicateUserLikedSongException(String message) {
        super(message);
    }
}
