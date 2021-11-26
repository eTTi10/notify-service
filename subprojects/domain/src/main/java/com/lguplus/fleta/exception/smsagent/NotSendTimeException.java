package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class NotSendTimeException extends NotifySmsRuntimeException {

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
