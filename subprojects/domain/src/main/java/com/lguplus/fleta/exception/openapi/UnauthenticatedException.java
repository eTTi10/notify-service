package com.lguplus.fleta.exception.openapi;

/**
 * Exception for error flag 1304.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class UnauthenticatedException extends RuntimeException {

    /**
     *
     */
    public UnauthenticatedException() {

        super();
    }

    /**
     * @param message
     */
    public UnauthenticatedException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public UnauthenticatedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public UnauthenticatedException(final Throwable cause) {

        super(cause);
    }
}
