package com.lguplus.fleta.exception.database;

/**
 * Exception for error flag 8999.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DatabaseException extends RuntimeException {

    /**
     *
     */
    public DatabaseException() {

        super();
    }

    /**
     *
     * @param message
     */
    public DatabaseException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DatabaseException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DatabaseException(final Throwable cause) {

        super(cause);
    }
}
