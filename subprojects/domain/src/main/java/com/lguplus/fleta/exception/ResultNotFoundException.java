package com.lguplus.fleta.exception;

/**
 * Exception for error flag 5010.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ResultNotFoundException extends RuntimeException {

    /**
     *
     */
    public ResultNotFoundException() {

        super();
    }

    /**
     * @param message
     */
    public ResultNotFoundException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ResultNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ResultNotFoundException(final Throwable cause) {

        super(cause);
    }
}
