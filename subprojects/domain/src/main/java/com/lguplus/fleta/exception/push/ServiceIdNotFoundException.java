package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1115.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ServiceIdNotFoundException extends RuntimeException {

    /**
     *
     */
    public ServiceIdNotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ServiceIdNotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ServiceIdNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ServiceIdNotFoundException(final Throwable cause) {

        super(cause);
    }
}
