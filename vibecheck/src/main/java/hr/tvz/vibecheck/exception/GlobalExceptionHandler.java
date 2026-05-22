package hr.tvz.vibecheck.exception;

import hr.tvz.vibecheck.exception.custom.*;
import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponse;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseService errorResponseService;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaylistNotFound(PlaylistNotFoundException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateFollowRequestException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateFollowRequestException(DuplicateFollowRequestException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.CONFLICT);
    }

    @ExceptionHandler(DuplicateFollowException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateFollowException(DuplicateFollowException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.CONFLICT);
    }

    @ExceptionHandler(FollowRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFollowRequestNotFoundException(FollowRequestNotFoundException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @ExceptionHandler(NotPendingStatusException.class)
    public ResponseEntity<ErrorResponse> handleNotPendingStatusException(NotPendingStatusException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.CONFLICT);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.CONFLICT);
    }

    @ExceptionHandler(DuplicateUserLikedSongException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserLikedSongException(DuplicateUserLikedSongException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.CONFLICT);
    }

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFollowNotFoundException(FollowNotFoundException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @ExceptionHandler(SelfFollowException.class)
    public ResponseEntity<ErrorResponse> handleSelfFollowException(SelfFollowException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @ExceptionHandler(NotPendingRequestException.class)
    public ResponseEntity<ErrorResponse> handleNotPendingException(NotPendingRequestException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @ExceptionHandler(NotYourRequestException.class)
    public ResponseEntity<ErrorResponse> handleNotYourRequestException(NotYourRequestException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.UNAUTHORIZED);
    }

    @ExceptionHandler(MailSendFailedException.class)
    public ResponseEntity<ErrorResponse> handleMailSendFailedException(MailSendFailedException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmptySpotifyTrackException.class)
    public ResponseEntity<ErrorResponse> handleEmptySpotifyTrackException(EmptySpotifyTrackException ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    // TODO: add @ExceptionHandler(MethodArgumentNotValidException.class) — the catch-all below intercepts it first,
    //  causing validation errors to return 500 instead of 400

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return errorResponseService.buildError(ex, request, ErrorKey.INTERNAL_SERVER_ERROR);
    }
}
