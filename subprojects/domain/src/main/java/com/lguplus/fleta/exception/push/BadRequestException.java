package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1104
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class BadRequestException extends RuntimeException {

    /**
     *
     */
    public BadRequestException() {

        super();
    }

    /**
     *
     * @param message
     */
    public BadRequestException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public BadRequestException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public BadRequestException(final Throwable cause) {

        super(cause);
    }
}
