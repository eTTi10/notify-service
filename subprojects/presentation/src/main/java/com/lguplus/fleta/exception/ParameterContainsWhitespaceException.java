package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5013.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterContainsWhitespaceException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterContainsWhitespaceException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterContainsWhitespaceException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterContainsWhitespaceException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterContainsWhitespaceException(final Throwable cause) {

        super(cause);
    }
}
