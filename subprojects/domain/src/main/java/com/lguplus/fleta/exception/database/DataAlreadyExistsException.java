package com.lguplus.fleta.exception.database;

/**
 * Exception for error flag 8001.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DataAlreadyExistsException extends RuntimeException {

    /**
     *
     */
    public DataAlreadyExistsException() {

        super();
    }

    /**
     *
     * @param message
     */
    public DataAlreadyExistsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DataAlreadyExistsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DataAlreadyExistsException(final Throwable cause) {

        super(cause);
    }
}
