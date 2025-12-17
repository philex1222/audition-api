package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global Exception Handler.
 * Uses RFC 7807 ProblemDetail to return standardized error responses.
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";

    @Autowired
    private transient AuditionLogger auditionLogger;

    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {
        final ProblemDetail problemDetail = createProblemDetail(e, e.getStatusCode());
        auditionLogger.logStandardProblemDetail(LOG, problemDetail, e);
        return problemDetail;
    }

    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException e) {
        final HttpStatusCode status = getHttpStatusCodeFromSystemException(e);
        final ProblemDetail problemDetail = createProblemDetail(e, status);
        auditionLogger.logStandardProblemDetail(LOG, problemDetail, e);
        return problemDetail;
    }

    // Handles validation errors (e.g. non-numeric IDs)
    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleValidationException(final ConstraintViolationException e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error");
        problemDetail.setDetail(e.getMessage());
        auditionLogger.logStandardProblemDetail(LOG, problemDetail, e);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleMainException(final Exception e) {
        final HttpStatusCode status = getHttpStatusCodeFromException(e);
        final ProblemDetail problemDetail = createProblemDetail(e, status);
        auditionLogger.logStandardProblemDetail(LOG, problemDetail, e);
        return problemDetail;
    }

    private ProblemDetail createProblemDetail(final Exception exception, final HttpStatusCode statusCode) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setDetail(getMessageFromException(exception));
        if (exception instanceof SystemException) {
            problemDetail.setTitle(((SystemException) exception).getTitle());
        } else {
            problemDetail.setTitle(DEFAULT_TITLE);
        }
        return problemDetail;
    }

    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_MESSAGE;
    }

    private HttpStatusCode getHttpStatusCodeFromSystemException(final SystemException exception) {
        try {
            if (exception.getStatusCode() != null) {
                return HttpStatusCode.valueOf(exception.getStatusCode());
            }
        } catch (final IllegalArgumentException iae) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Provided status code {} is invalid, defaulting to 500", exception.getStatusCode());
            }
        }
        return INTERNAL_SERVER_ERROR;
    }

    private HttpStatusCode getHttpStatusCodeFromException(final Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            return ((HttpClientErrorException) exception).getStatusCode();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        }
        return INTERNAL_SERVER_ERROR;
    }
}



