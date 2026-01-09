package com.audition.configuration;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Filter to inject Trace and Span IDs into HTTP Response headers. This allows clients to report these IDs back to
 * support teams for easier log correlation.
 */
@Component
public class ResponseHeaderInjector implements Filter {

    @Autowired
    private transient Tracer tracer;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        if (response instanceof HttpServletResponse && tracer.currentSpan() != null) {
            final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            final var context = tracer.currentSpan().context();
            // Standard headers for distributed tracing
            httpServletResponse.setHeader("X-Trace-Id", context.traceId());
            httpServletResponse.setHeader("X-Span-Id", context.spanId());
        }

        chain.doFilter(request, response);
    }
}