package com.lguplus.fleta.exception.database;

/**
 * Exception for error flag 8002.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DataNotExistsException extends RuntimeException {

    /**
     *
     */
    public DataNotExistsException() {

        super();
    }

    /**
     *
     * @param message
     */
    public DataNotExistsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DataNotExistsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DataNotExistsException(final Throwable cause) {

        super(cause);
    }
}
