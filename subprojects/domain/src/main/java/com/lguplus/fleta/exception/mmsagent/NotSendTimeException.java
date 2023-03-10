package com.lguplus.fleta.exception.mmsagent;

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

    @Override
    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

}
