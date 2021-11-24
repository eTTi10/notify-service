package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1113
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotExistRegistIdException extends NotifyRuntimeException {

    /**
     *
     */
    public NotExistRegistIdException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotExistRegistIdException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotExistRegistIdException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotExistRegistIdException(final Throwable cause) {

        super(cause);
    }
}
