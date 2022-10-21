package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5017.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterLengthTooShortException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterLengthTooShortException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterLengthTooShortException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterLengthTooShortException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterLengthTooShortException(final Throwable cause) {

        super(cause);
    }
}
