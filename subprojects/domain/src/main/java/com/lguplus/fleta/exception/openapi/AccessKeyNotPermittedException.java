package com.lguplus.fleta.exception.openapi;

/**
 * Exception for error flag 1300.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class AccessKeyNotPermittedException extends RuntimeException {

    /**
     *
     */
    public AccessKeyNotPermittedException() {

        super();
    }

    /**
     * @param message
     */
    public AccessKeyNotPermittedException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AccessKeyNotPermittedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public AccessKeyNotPermittedException(final Throwable cause) {

        super(cause);
    }
}
