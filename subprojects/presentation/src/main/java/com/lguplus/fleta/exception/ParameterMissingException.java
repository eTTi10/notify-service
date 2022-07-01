package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5000.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterMissingException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterMissingException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterMissingException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterMissingException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterMissingException(final Throwable cause) {

        super(cause);
    }
}
