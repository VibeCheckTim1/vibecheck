package hr.tvz.vibecheck.exception.custom;

public class FollowRequestNotFoundException extends RuntimeException {
    public FollowRequestNotFoundException(String message) {
        super(message);
    }
}
