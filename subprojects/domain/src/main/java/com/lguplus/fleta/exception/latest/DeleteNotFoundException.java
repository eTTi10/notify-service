package com.lguplus.fleta.exception.latest;

/**
 * Exception for error flag 1401.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DeleteNotFoundException extends RuntimeException {

    /**
     *
     */
    public DeleteNotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public DeleteNotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DeleteNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DeleteNotFoundException(final Throwable cause) {

        super(cause);
    }
}
