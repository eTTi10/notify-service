package com.lguplus.fleta.exception.openapi;

/**
 * Exception for error flag 1303.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class UrlNotPermittedException extends RuntimeException {

    /**
     *
     */
    public UrlNotPermittedException() {

        super();
    }

    /**
     * @param message
     */
    public UrlNotPermittedException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public UrlNotPermittedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public UrlNotPermittedException(final Throwable cause) {

        super(cause);
    }
}
