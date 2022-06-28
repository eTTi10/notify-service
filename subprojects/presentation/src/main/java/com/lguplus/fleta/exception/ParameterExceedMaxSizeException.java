package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5012.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterExceedMaxSizeException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterExceedMaxSizeException() {

        super();
    }

    /**
     * @param message
     */
    public ParameterExceedMaxSizeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ParameterExceedMaxSizeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ParameterExceedMaxSizeException(final Throwable cause) {

        super(cause);
    }
}
