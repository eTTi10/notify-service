package com.lguplus.fleta.exception.stat;

/**
 * Exception for error flag 1420.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class StatNotFoundException extends RuntimeException {

    /**
     *
     */
    public StatNotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public StatNotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public StatNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public StatNotFoundException(final Throwable cause) {

        super(cause);
    }
}
