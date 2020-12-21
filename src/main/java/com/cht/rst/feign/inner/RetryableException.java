package com.cht.rst.feign.inner;

import org.springframework.http.HttpMethod;

import java.util.Date;

public class RetryableException extends FeignException {

    private static final long serialVersionUID = 1L;

    private final Long retryAfter;
    private final HttpMethod httpMethod;

    /**
     * @param retryAfter usually corresponds to the {@link Util#RETRY_AFTER} header.
     */
    public RetryableException(String message, HttpMethod httpMethod, Throwable cause,
                              Date retryAfter) {
        super(message, cause);
        this.httpMethod = httpMethod;
        this.retryAfter = retryAfter != null ? retryAfter.getTime() : null;
    }

    /**
     * @param retryAfter usually corresponds to the {@link Util#RETRY_AFTER} header.
     */
    public RetryableException(String message, HttpMethod httpMethod, Date retryAfter) {
        super(message);
        this.httpMethod = httpMethod;
        this.retryAfter = retryAfter != null ? retryAfter.getTime() : null;
    }

    /**
     * Sometimes corresponds to the {@link Util#RETRY_AFTER} header present in {@code 503}
     * status. Other times parsed from an application-specific response. Null if unknown.
     */
    public Date retryAfter() {
        return retryAfter != null ? new Date(retryAfter) : null;
    }

    public HttpMethod method() {
        return this.httpMethod;
    }
}
