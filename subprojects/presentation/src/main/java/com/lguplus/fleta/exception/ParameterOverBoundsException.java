package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5006.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterOverBoundsException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterOverBoundsException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ParameterOverBoundsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ParameterOverBoundsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ParameterOverBoundsException(final Throwable cause) {

        super(cause);
    }
}
