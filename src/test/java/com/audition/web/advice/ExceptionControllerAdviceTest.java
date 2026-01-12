package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerAdviceTest {

    @Mock
    private transient AuditionLogger auditionLogger;

    @InjectMocks
    private transient ExceptionControllerAdvice advice;

    @Test
    void testHandleHttpClientException() {
        final HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        final ProblemDetail pd = advice.handleHttpClientException(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
        assertEquals("API Error Occurred", pd.getTitle());
        verify(auditionLogger).logStandardProblemDetail(any(), any(), any());
    }

    @Test
    void testHandleSystemException() {
        final SystemException ex = new SystemException("Detail", "Custom Title", 400);
        final ProblemDetail pd = advice.handleSystemException(ex);

        assertEquals(400, pd.getStatus());
        assertEquals("Custom Title", pd.getTitle());
        assertEquals("Detail", pd.getDetail());
    }

    @Test
    void testHandleSystemExceptionWithInvalidStatus() {
        final SystemException ex = new SystemException("Detail", "Title", 9999);
        final ProblemDetail pd = advice.handleSystemException(ex);

        assertEquals(500, pd.getStatus());
    }

    @Test
    void testHandleValidationException() {
        final ConstraintViolationException ex = new ConstraintViolationException("Validation failed", null);
        final ProblemDetail pd = advice.handleValidationException(ex);

        assertEquals(400, pd.getStatus());
        assertEquals("Validation Error", pd.getTitle());
    }

    @Test
    void testHandleMainExceptionMethodNotAllowed() {
        final HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        final ProblemDetail pd = advice.handleMainException(ex);

        assertEquals(405, pd.getStatus());
    }

    @Test
    void testHandleMainExceptionGeneric() {
        final RuntimeException ex = new RuntimeException("Generic Error");
        final ProblemDetail pd = advice.handleMainException(ex);

        assertEquals(500, pd.getStatus());
        assertEquals("Generic Error", pd.getDetail());
    }

    @Test
    void testHandleMainExceptionNullMessage() {
        final RuntimeException ex = new RuntimeException((String) null);
        final ProblemDetail pd = advice.handleMainException(ex);

        assertEquals(500, pd.getStatus());
        assertNotNull(pd.getDetail());
    }
}