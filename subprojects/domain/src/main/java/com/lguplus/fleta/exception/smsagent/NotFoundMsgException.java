package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class NotFoundMsgException extends NotifyRuntimeException {

    public NotFoundMsgException() {
        super();
    }

    /**
     * @param message
     */
    public NotFoundMsgException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public NotFoundMsgException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public NotFoundMsgException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

}
