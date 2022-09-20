package com.lguplus.fleta.exception.musicshow;

import javax.validation.Payload;

/**
 * Exception for error flag 5010.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParameterOutOfRangeException extends RuntimeException implements Payload {

    /**
     *
     */
    public ParameterOutOfRangeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ParameterOutOfRangeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ParameterOutOfRangeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ParameterOutOfRangeException(final Throwable cause) {

        super(cause);
    }
}
