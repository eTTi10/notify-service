package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class SystemBusyException extends NotifySmsRuntimeException {


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
