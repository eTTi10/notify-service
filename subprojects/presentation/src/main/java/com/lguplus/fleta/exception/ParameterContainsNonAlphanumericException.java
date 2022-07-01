package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5014.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterContainsNonAlphanumericException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterContainsNonAlphanumericException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterContainsNonAlphanumericException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterContainsNonAlphanumericException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterContainsNonAlphanumericException(final Throwable cause) {

        super(cause);
    }
}
