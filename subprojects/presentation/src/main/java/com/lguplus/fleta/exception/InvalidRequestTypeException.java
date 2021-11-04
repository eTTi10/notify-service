package com.lguplus.fleta.exception;

import javax.validation.Payload;

/**
 * Exception for error flag 5001.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class InvalidRequestTypeException extends RuntimeException implements Payload {

    /**
     *
     */
    public InvalidRequestTypeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InvalidRequestTypeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InvalidRequestTypeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InvalidRequestTypeException(final Throwable cause) {

        super(cause);
    }
}
