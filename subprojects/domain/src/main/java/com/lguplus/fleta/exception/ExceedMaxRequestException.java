package com.lguplus.fleta.exception;

/**
 * Exception for error flag 1201.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExceedMaxRequestException extends RuntimeException {

    /**
     *
     */
    public ExceedMaxRequestException() {

        super();
    }

    /**
     * @param message
     */
    public ExceedMaxRequestException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ExceedMaxRequestException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ExceedMaxRequestException(final Throwable cause) {

        super(cause);
    }
}
