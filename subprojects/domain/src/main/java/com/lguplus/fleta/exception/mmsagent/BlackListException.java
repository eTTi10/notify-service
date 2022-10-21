package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class BlackListException extends NotifyRuntimeException {

    public BlackListException() {
        super();
    }

    /**
     * @param message
     */
    public BlackListException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BlackListException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public BlackListException(final Throwable cause) {

        super(cause);
    }

    @Override
    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

}
