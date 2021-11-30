package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class NotFoundMsgException extends NotifySmsRuntimeException {

    public NotFoundMsgException() {
        super();
    }

    /**
     *
     * @param message
     */
    public NotFoundMsgException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotFoundMsgException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotFoundMsgException(final Throwable cause) {

        super(cause);
    }

}
