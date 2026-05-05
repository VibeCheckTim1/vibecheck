package hr.tvz.vibecheck.exception.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorResponseServiceTest {

    @Mock
    private ErrorProperties errorProperties;

    @Mock
    private ObjectMapper objectMapper;

    private ErrorResponseService errorResponseService;

    @BeforeEach
    void setUp() {
        errorResponseService = new ErrorResponseService(errorProperties, objectMapper);
        MDC.clear();
    }

    @Test
    void buildError_withErrorKey_shouldReturnResponseEntity() {
        when(errorProperties.get(ErrorKey.NOT_FOUND)).thenReturn(new ErrorEntry(404, "Not found."));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/resource");
        Exception ex = new RuntimeException("Resource missing");

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().status()).isEqualTo(404);
        assertThat(result.getBody().message()).isEqualTo("Resource missing");
        assertThat(result.getBody().uri()).isEqualTo("/api/resource");
    }

    @Test
    void buildError_withNullExceptionMessage_shouldUseErrorEntryMessage() {
        when(errorProperties.get(ErrorKey.NOT_FOUND)).thenReturn(new ErrorEntry(404, "Default not found."));
        MockHttpServletRequest request = new MockHttpServletRequest();
        Exception ex = new RuntimeException((String) null);

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, ErrorKey.NOT_FOUND);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message()).isEqualTo("Default not found.");
    }

    @Test
    void buildError_withNullExceptionAndNullEntryMessage_shouldUseHttpStatusReasonPhrase() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ErrorEntry entryWithNullMessage = new ErrorEntry(404, null);
        Exception ex = new RuntimeException((String) null);

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, entryWithNullMessage);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message()).isEqualTo("Not Found");
    }

    @Test
    void buildError_withNoSidInMDC_shouldUseDefaultSid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ErrorEntry entry = new ErrorEntry(500, "Server error.");
        Exception ex = new RuntimeException("error");

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, entry);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().sid()).isEqualTo("00000000000000000000000000000000");
    }

    @Test
    void buildError_withBlankSidInMDC_shouldUseDefaultSid() {
        MDC.put("sid", "   ");
        MockHttpServletRequest request = new MockHttpServletRequest();
        ErrorEntry entry = new ErrorEntry(500, "Server error.");
        Exception ex = new RuntimeException("error");

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, entry);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().sid()).isEqualTo("00000000000000000000000000000000");
    }

    @Test
    void buildError_withSidInMDC_shouldUseExistingSid() {
        MDC.put("sid", "existing-session-id");
        MockHttpServletRequest request = new MockHttpServletRequest();
        ErrorEntry entry = new ErrorEntry(400, "Bad request.");
        Exception ex = new RuntimeException("error");

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, entry);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().sid()).isEqualTo("existing-session-id");
    }

    @Test
    void buildError_withErrorEntryDirectly_shouldReturnResponseEntity() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ErrorEntry entry = new ErrorEntry(400, "Bad request.");
        Exception ex = new RuntimeException("validation failed");

        ResponseEntity<ErrorResponse> result = errorResponseService.buildError(ex, request, entry);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message()).isEqualTo("validation failed");
    }

    @Test
    void writeError_shouldWriteErrorResponseToServletResponse() throws IOException {
        when(errorProperties.get(ErrorKey.NOT_FOUND)).thenReturn(new ErrorEntry(404, "Not found."));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Exception ex = new RuntimeException("not found");

        errorResponseService.writeError(request, response, ex, ErrorKey.NOT_FOUND);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getContentType()).isEqualTo("application/json");
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }
}
