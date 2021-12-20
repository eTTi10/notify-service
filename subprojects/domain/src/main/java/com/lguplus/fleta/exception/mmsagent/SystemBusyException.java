package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

public class SystemBusyException extends NotifyMmsRuntimeException {


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
