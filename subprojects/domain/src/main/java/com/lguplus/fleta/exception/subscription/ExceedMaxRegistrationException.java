package com.lguplus.fleta.exception.subscription;

/**
 * Exception for error flag 1431.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExceedMaxRegistrationException extends RuntimeException {

    /**
     *
     */
    public ExceedMaxRegistrationException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ExceedMaxRegistrationException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ExceedMaxRegistrationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ExceedMaxRegistrationException(final Throwable cause) {

        super(cause);
    }
}
