package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class MsgTypeErrorException extends NotifySmsRuntimeException {


    public MsgTypeErrorException() {
        super();
    }

    /**
     *
     * @param message
     */
    public MsgTypeErrorException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MsgTypeErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public MsgTypeErrorException(final Throwable cause) {

        super(cause);
    }


}
