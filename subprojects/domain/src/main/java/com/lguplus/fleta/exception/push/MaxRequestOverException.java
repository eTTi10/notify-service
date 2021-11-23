package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1120
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class MaxRequestOverException extends RuntimeException {

    /**
     *
     */
    public MaxRequestOverException() {

        super();
    }

    /**
     *
     * @param message
     */
    public MaxRequestOverException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MaxRequestOverException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public MaxRequestOverException(final Throwable cause) {

        super(cause);
    }
}
