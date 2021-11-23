package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1103
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SocketTimeException extends RuntimeException {

    /**
     *
     */
    public SocketTimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SocketTimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SocketTimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SocketTimeException(final Throwable cause) {

        super(cause);
    }
}
