package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 1502.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterDatabaseException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterDatabaseException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ParameterDatabaseException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ParameterDatabaseException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ParameterDatabaseException(final Throwable cause) {
        super(cause);
    }
}
