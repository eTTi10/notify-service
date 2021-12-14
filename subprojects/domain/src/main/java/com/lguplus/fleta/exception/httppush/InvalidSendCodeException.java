package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;

public class InvalidSendCodeException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public InvalidSendCodeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InvalidSendCodeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InvalidSendCodeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InvalidSendCodeException(final Throwable cause) {

        super(cause);
    }
}
