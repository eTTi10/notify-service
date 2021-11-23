package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1106
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ForbiddenException extends RuntimeException {

    /**
     *
     */
    public ForbiddenException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ForbiddenException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ForbiddenException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ForbiddenException(final Throwable cause) {

        super(cause);
    }
}
