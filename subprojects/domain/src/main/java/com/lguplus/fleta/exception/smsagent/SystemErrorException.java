package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class SystemErrorException extends NotifySmsRuntimeException {


    public SystemErrorException() {
        super();
    }

    /**
     *
     * @param message
     */
    public SystemErrorException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SystemErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SystemErrorException(final Throwable cause) {

        super(cause);
    }


}
