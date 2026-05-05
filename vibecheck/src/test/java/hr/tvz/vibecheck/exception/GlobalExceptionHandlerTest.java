package hr.tvz.vibecheck.exception;

import hr.tvz.vibecheck.exception.custom.*;
import hr.tvz.vibecheck.exception.framework.ErrorKey;
import hr.tvz.vibecheck.exception.framework.ErrorResponse;
import hr.tvz.vibecheck.exception.framework.ErrorResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ErrorResponseService errorResponseService;

    private GlobalExceptionHandler handler;

    private MockHttpServletRequest request;
    private ResponseEntity<ErrorResponse> mockResponse;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(errorResponseService);
        request = new MockHttpServletRequest();
        mockResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("sid", "/uri", 400, "Error", LocalDateTime.now()));
    }

    @Test
    void handleUserNotFound_shouldCallBuildErrorWithNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.NOT_FOUND))).thenReturn(mockResponse);

        handler.handleUserNotFound(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @Test
    void handleInvalidPassword_shouldCallBuildErrorWithBadRequest() {
        InvalidPasswordException ex = new InvalidPasswordException();
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.BAD_REQUEST))).thenReturn(mockResponse);

        handler.handleInvalidPassword(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @Test
    void handleInvalidFile_shouldCallBuildErrorWithBadRequest() {
        InvalidFileException ex = new InvalidFileException("Invalid file");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.BAD_REQUEST))).thenReturn(mockResponse);

        handler.handleInvalidFile(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @Test
    void handleDuplicateFollowRequest_shouldCallBuildErrorWithConflict() {
        DuplicateFollowRequestException ex = new DuplicateFollowRequestException("Duplicate follow request");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.CONFLICT))).thenReturn(mockResponse);

        handler.handleDuplicateFollowRequestException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.CONFLICT);
    }

    @Test
    void handleDuplicateFollow_shouldCallBuildErrorWithConflict() {
        DuplicateFollowException ex = new DuplicateFollowException("Duplicate follow");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.CONFLICT))).thenReturn(mockResponse);

        handler.handleDuplicateFollowException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.CONFLICT);
    }

    @Test
    void handleFollowRequestNotFound_shouldCallBuildErrorWithNotFound() {
        FollowRequestNotFoundException ex = new FollowRequestNotFoundException("Follow request not found");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.NOT_FOUND))).thenReturn(mockResponse);

        handler.handleFollowRequestNotFoundException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @Test
    void handleNotPendingStatus_shouldCallBuildErrorWithConflict() {
        NotPendingStatusException ex = new NotPendingStatusException("Not pending");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.CONFLICT))).thenReturn(mockResponse);

        handler.handleNotPendingStatusException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.CONFLICT);
    }

    @Test
    void handleDuplicateUser_shouldCallBuildErrorWithConflict() {
        DuplicateUserException ex = new DuplicateUserException("Duplicate user");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.CONFLICT))).thenReturn(mockResponse);

        handler.handleDuplicateUserException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.CONFLICT);
    }

    @Test
    void handleFollowNotFound_shouldCallBuildErrorWithNotFound() {
        FollowNotFoundException ex = new FollowNotFoundException("Follow not found");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.NOT_FOUND))).thenReturn(mockResponse);

        handler.handleFollowNotFoundException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.NOT_FOUND);
    }

    @Test
    void handleSelfFollow_shouldCallBuildErrorWithBadRequest() {
        SelfFollowException ex = new SelfFollowException("Cannot follow self");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.BAD_REQUEST))).thenReturn(mockResponse);

        handler.handleSelfFollowException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @Test
    void handleNotPendingRequest_shouldCallBuildErrorWithBadRequest() {
        NotPendingRequestException ex = new NotPendingRequestException("Not pending request");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.BAD_REQUEST))).thenReturn(mockResponse);

        handler.handleNotPendingException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @Test
    void handleNotYourRequest_shouldCallBuildErrorWithForbidden() {
        NotYourRequestException ex = new NotYourRequestException("Not your request");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.FORBIDDEN))).thenReturn(mockResponse);

        handler.handleNotYourRequestException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.FORBIDDEN);
    }

    @Test
    void handleIllegalArgument_shouldCallBuildErrorWithBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.BAD_REQUEST))).thenReturn(mockResponse);

        handler.handleIllegalArgumentException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.BAD_REQUEST);
    }

    @Test
    void handleAuthenticationCredentialsNotFound_shouldCallBuildErrorWithUnauthorized() {
        AuthenticationCredentialsNotFoundException ex = new AuthenticationCredentialsNotFoundException("Not authenticated");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.UNAUTHORIZED))).thenReturn(mockResponse);

        handler.handleAuthenticationCredentialsNotFoundException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.UNAUTHORIZED);
    }

    @Test
    void handleGenericException_shouldCallBuildErrorWithInternalServerError() {
        Exception ex = new Exception("Unexpected error");
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.INTERNAL_SERVER_ERROR))).thenReturn(mockResponse);

        handler.handleException(ex, request);

        verify(errorResponseService).buildError(ex, request, ErrorKey.INTERNAL_SERVER_ERROR);
    }

    @Test
    void allHandlers_shouldReturnResponseEntityFromService() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<ErrorResponse> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("sid", "/api", 404, "Not found", LocalDateTime.now()));
        when(errorResponseService.buildError(any(), any(), eq(ErrorKey.NOT_FOUND))).thenReturn(expected);

        ResponseEntity<ErrorResponse> result = handler.handleUserNotFound(ex, request);

        assertThat(result).isEqualTo(expected);
    }
}
