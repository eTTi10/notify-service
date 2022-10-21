package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5002.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterLengthOverLimitException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterLengthOverLimitException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterLengthOverLimitException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterLengthOverLimitException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterLengthOverLimitException(final Throwable cause) {

        super(cause);
    }
}
