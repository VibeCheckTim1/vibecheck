package hr.tvz.vibecheck.exception;

public class DuplicateFollowRequestException extends RuntimeException {
    public DuplicateFollowRequestException(String message) {
        super(message);
    }
}
