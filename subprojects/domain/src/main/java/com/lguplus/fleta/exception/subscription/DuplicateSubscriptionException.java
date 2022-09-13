package com.lguplus.fleta.exception.subscription;

/**
 * Exception for error flag 1432.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DuplicateSubscriptionException extends RuntimeException {

    /**
     *
     */
    public DuplicateSubscriptionException() {

        super();
    }

    /**
     * @param message
     */
    public DuplicateSubscriptionException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateSubscriptionException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public DuplicateSubscriptionException(final Throwable cause) {

        super(cause);
    }
}
