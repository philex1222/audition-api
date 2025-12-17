package com.audition.common.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public void warn(final Logger logger, final String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void error(final Logger logger, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    /**
     * Standardized logging for RFC 7807 ProblemDetails.
     * Ensures that all API errors are logged with consistent structure including Status, Title, and Detail.
     */
    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message));
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        if (standardProblemDetail == null) {
            return StringUtils.EMPTY;
        }
        // Formats the ProblemDetail into a single loggable string
        return String.format("ProblemDetail: Status=%s, Title='%s', Detail='%s', Instance='%s'",
                standardProblemDetail.getStatus(),
                standardProblemDetail.getTitle(),
                standardProblemDetail.getDetail(),
                standardProblemDetail.getInstance());
    }

    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        return String.format("Error Code: %d, Message: %s", errorCode, message);
    }
}
