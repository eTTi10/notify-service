package com.lguplus.fleta.exception.database;

/**
 * Exception for error flag 8000.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DuplicateKeyException extends RuntimeException {

    /**
     *
     */
    public DuplicateKeyException() {

        super();
    }

    /**
     * @param message
     */
    public DuplicateKeyException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateKeyException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public DuplicateKeyException(final Throwable cause) {

        super(cause);
    }
}
