package hr.tvz.vibecheck.exception;

import hr.tvz.vibecheck.exception.custom.InvalidFileException;
import hr.tvz.vibecheck.exception.custom.InvalidPasswordException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponse;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseService errorResponseService;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<?> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.INTERNAL_SERVER_ERROR);
    }
}
