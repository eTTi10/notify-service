package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class NoHttpsException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

    public NoHttpsException() {
        super();
    }

    /**
     *
     * @param message
     */
    public NoHttpsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoHttpsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoHttpsException(final Throwable cause) {

        super(cause);
    }


}
