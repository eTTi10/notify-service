package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2007.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class InvalidOrdException extends RuntimeException {

    /**
     *
     */
    public InvalidOrdException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InvalidOrdException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InvalidOrdException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InvalidOrdException(final Throwable cause) {

        super(cause);
    }
}
