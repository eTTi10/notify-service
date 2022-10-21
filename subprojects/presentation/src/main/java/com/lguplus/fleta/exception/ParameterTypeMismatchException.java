package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5008.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterTypeMismatchException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterTypeMismatchException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterTypeMismatchException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterTypeMismatchException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterTypeMismatchException(final Throwable cause) {

        super(cause);
    }
}
