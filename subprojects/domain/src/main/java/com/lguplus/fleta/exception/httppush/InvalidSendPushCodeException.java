package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;


/**
 * Exception for error flag 9990.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class InvalidSendPushCodeException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public InvalidSendPushCodeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InvalidSendPushCodeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InvalidSendPushCodeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InvalidSendPushCodeException(final Throwable cause) {

        super(cause);
    }
}
