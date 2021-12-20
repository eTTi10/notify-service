package com.lguplus.fleta.exception.openapi;

/**
 * Exception for error flag 1302.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class InvalidExpireTimeException extends RuntimeException {

    /**
     *
     */
    public InvalidExpireTimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InvalidExpireTimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InvalidExpireTimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InvalidExpireTimeException(final Throwable cause) {

        super(cause);
    }
}
