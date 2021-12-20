package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class SystemErrorException extends NotifyMmsRuntimeException {


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
