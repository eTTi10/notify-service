package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

public class NotSendTimeException extends NotifyMmsRuntimeException {

    public NotSendTimeException() {
        super();
    }

    /**
     *
     * @param message
     */
    public NotSendTimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotSendTimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotSendTimeException(final Throwable cause) {

        super(cause);
    }

}
