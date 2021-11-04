package com.lguplus.fleta.exception;

/**
 * Exception for error flag 4000.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NotChangedException extends RuntimeException {

    /**
     *
     */
    public NotChangedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotChangedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotChangedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotChangedException(final Throwable cause) {

        super(cause);
    }
}
