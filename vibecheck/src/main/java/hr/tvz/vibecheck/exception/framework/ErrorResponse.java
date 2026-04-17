package hr.tvz.vibecheck.exception.framework;

import java.time.LocalDateTime;

public record ErrorResponse(
        String sid,
        String uri,
        int status,
        String message,
        LocalDateTime timestamp
) {
}
