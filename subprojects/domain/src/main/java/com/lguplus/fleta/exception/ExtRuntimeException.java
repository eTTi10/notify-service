package com.lguplus.fleta.exception;

/**
 * Exception for error flag 9999.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExtRuntimeException extends RuntimeException {

    /**
     *
     */
    public ExtRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ExtRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ExtRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ExtRuntimeException(final Throwable cause) {

        super(cause);
    }
}
