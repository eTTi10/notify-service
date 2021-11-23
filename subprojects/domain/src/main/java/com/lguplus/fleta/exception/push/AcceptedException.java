package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class AcceptedException extends RuntimeException {

    /**
     *
     */
    public AcceptedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public AcceptedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public AcceptedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public AcceptedException(final Throwable cause) {

        super(cause);
    }
}
