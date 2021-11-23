package com.lguplus.fleta.exception.push;

/**
 * Exception for error flag 1114
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ExceptionOccursException extends RuntimeException {

    /**
     *
     */
    public ExceptionOccursException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ExceptionOccursException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ExceptionOccursException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ExceptionOccursException(final Throwable cause) {

        super(cause);
    }
}
