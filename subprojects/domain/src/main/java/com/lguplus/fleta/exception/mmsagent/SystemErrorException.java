package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class SystemErrorException extends NotifyRuntimeException {

    public SystemErrorException() {
        super();
    }

    /**
     * @param message
     */
    public SystemErrorException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SystemErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public SystemErrorException(final Throwable cause) {

        super(cause);
    }

    @Override
    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }


}
