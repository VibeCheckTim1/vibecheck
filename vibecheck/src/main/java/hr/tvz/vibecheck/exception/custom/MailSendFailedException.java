package hr.tvz.vibecheck.exception.custom;

public class MailSendFailedException extends RuntimeException {
    public MailSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
