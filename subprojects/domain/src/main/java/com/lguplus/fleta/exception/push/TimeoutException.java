package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1116
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class TimeoutException extends RuntimeException {

    /**
     *
     */
    public TimeoutException() {

        super();
    }

    /**
     *
     * @param message
     */
    public TimeoutException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public TimeoutException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public TimeoutException(final Throwable cause) {

        super(cause);
    }
}
