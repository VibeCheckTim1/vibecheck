package hr.tvz.vibecheck.exception.custom;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) {
        super(message);
    }
}