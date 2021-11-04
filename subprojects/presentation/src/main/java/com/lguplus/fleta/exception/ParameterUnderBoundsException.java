package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5003.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterUnderBoundsException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterUnderBoundsException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ParameterUnderBoundsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ParameterUnderBoundsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ParameterUnderBoundsException(final Throwable cause) {

        super(cause);
    }
}
