package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 1109
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class InternalErrorException extends NotifyPushRuntimeException {

    /**
     *
     */
    public InternalErrorException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InternalErrorException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InternalErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InternalErrorException(final Throwable cause) {

        super(cause);
    }
}
