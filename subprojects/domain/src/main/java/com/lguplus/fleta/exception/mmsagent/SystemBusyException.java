package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class SystemBusyException extends NotifyRuntimeException {


    @Override
    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

    public SystemBusyException() {
        super();
    }

    /**
     *
     * @param message
     */
    public SystemBusyException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SystemBusyException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SystemBusyException(final Throwable cause) {

        super(cause);
    }


}
