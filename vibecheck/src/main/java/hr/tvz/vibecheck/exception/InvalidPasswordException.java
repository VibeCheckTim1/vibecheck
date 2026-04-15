package hr.tvz.vibecheck.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Old password is incorrect");
    }
}
