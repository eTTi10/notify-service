package com.lguplus.fleta.exception.openapi;

/**
 * Exception for error flag 1301.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class CpIdNotPermittedException extends RuntimeException {

    /**
     *
     */
    public CpIdNotPermittedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public CpIdNotPermittedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public CpIdNotPermittedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public CpIdNotPermittedException(final Throwable cause) {

        super(cause);
    }
}
