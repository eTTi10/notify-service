package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class NotSendTimeException extends NotifyRuntimeException {

    public NotSendTimeException() {
        super();
    }

    /**
     * @param message
     */
    public NotSendTimeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public NotSendTimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public NotSendTimeException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

}
