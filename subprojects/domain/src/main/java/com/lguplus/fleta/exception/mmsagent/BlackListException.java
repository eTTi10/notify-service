package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

public class BlackListException extends NotifyMmsRuntimeException {

    public BlackListException() {
        super();
    }

    /**
     *
     * @param message
     */
    public BlackListException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public BlackListException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public BlackListException(final Throwable cause) {

        super(cause);
    }

}
