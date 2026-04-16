package hr.tvz.vibecheck.exception;

public class FollowRequestNotFoundException extends RuntimeException {
    public FollowRequestNotFoundException(String message) {
        super(message);
    }
}
