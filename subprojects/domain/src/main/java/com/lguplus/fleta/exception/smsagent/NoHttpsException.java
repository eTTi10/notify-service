package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class NoHttpsException extends NotifySmsRuntimeException {


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
