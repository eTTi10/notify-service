package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class MsgTypeErrorException extends NotifyRuntimeException {

    public MsgTypeErrorException() {
        super();
    }

    /**
     * @param message
     */
    public MsgTypeErrorException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MsgTypeErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public MsgTypeErrorException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }


}
