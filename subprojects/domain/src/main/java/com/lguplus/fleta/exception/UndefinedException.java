package com.lguplus.fleta.exception;

/**
 * Exception for undefined.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class UndefinedException extends RuntimeException {

    /**
     *
     */
    public UndefinedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public UndefinedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public UndefinedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public UndefinedException(final Throwable cause) {

        super(cause);
    }
}
