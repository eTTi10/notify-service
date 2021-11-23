package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1105
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class UnAuthorizedException extends RuntimeException {

    /**
     *
     */
    public UnAuthorizedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public UnAuthorizedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public UnAuthorizedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public UnAuthorizedException(final Throwable cause) {

        super(cause);
    }
}
