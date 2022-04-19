package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5005.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterLengthUnderLimitException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterLengthUnderLimitException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ParameterLengthUnderLimitException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ParameterLengthUnderLimitException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ParameterLengthUnderLimitException(final Throwable cause) {

        super(cause);
    }
}
