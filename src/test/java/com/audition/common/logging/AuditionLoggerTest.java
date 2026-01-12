package com.audition.common.logging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;

class AuditionLoggerTest {

    private static final String TEST_MESSAGE = "test message";
    private transient AuditionLogger auditionLogger;
    private transient Logger mockLogger;

    @BeforeEach
    void setUp() {
        auditionLogger = new AuditionLogger();
        mockLogger = mock(Logger.class);
    }

    @Test
    void testInfo() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(mockLogger, TEST_MESSAGE);
        verify(mockLogger, times(1)).info(TEST_MESSAGE);
    }

    @Test
    void testInfoWithObject() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(mockLogger, TEST_MESSAGE, "obj");
        verify(mockLogger, times(1)).info(TEST_MESSAGE, "obj");
    }

    @Test
    void testDebug() {
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        auditionLogger.debug(mockLogger, TEST_MESSAGE);
        verify(mockLogger, times(1)).debug(TEST_MESSAGE);
    }

    @Test
    void testWarn() {
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        auditionLogger.warn(mockLogger, TEST_MESSAGE);
        verify(mockLogger, times(1)).warn(TEST_MESSAGE);
    }

    @Test
    void testError() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.error(mockLogger, TEST_MESSAGE);
        verify(mockLogger, times(1)).error(TEST_MESSAGE);
    }

    @Test
    void testLogErrorWithException() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        final Exception ex = new Exception("oops");
        auditionLogger.logErrorWithException(mockLogger, TEST_MESSAGE, ex);
        verify(mockLogger, times(1)).error(TEST_MESSAGE, ex);
    }

    @Test
    void testLogStandardProblemDetail() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        final ProblemDetail pd = ProblemDetail.forStatus(500);
        pd.setTitle("Title");
        pd.setDetail("Detail");

        auditionLogger.logStandardProblemDetail(mockLogger, pd, new Exception());

        verify(mockLogger).error(org.mockito.ArgumentMatchers.contains("ProblemDetail"), any(Exception.class));
    }

    @Test
    void testLogStandardProblemDetailNull() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logStandardProblemDetail(mockLogger, null, new Exception());
        verify(mockLogger).error(eq(""), any(Exception.class));
    }

    @Test
    void testLogHttpStatusCodeError() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logHttpStatusCodeError(mockLogger, "message", 404);
        verify(mockLogger).error(org.mockito.ArgumentMatchers.contains("Error Code: 404"));
    }

    @Test
    void testLoggingDisabled() {
        when(mockLogger.isInfoEnabled()).thenReturn(false);
        auditionLogger.info(mockLogger, "test");
        verify(mockLogger, never()).info("test");
    }
}