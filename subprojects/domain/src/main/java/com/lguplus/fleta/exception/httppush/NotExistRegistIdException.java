package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 1113
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotExistRegistIdException extends NotifyPushRuntimeException {

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
