package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

/**
 * Exception for error flag 1102
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SocketException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

    /**
     *
     */
    public SocketException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SocketException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SocketException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SocketException(final Throwable cause) {

        super(cause);
    }
}
