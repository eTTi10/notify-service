package com.lguplus.fleta.exception.subscription;

/**
 * Exception for error flag 1430.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class SubscriberAlreadyExistsException extends RuntimeException {

    /**
     *
     */
    public SubscriberAlreadyExistsException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SubscriberAlreadyExistsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SubscriberAlreadyExistsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SubscriberAlreadyExistsException(final Throwable cause) {

        super(cause);
    }
}
