package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotifySmsRuntimeException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

    /**
     *
     */
    public NotifySmsRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotifySmsRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotifySmsRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotifySmsRuntimeException(final Throwable cause) {

        super(cause);
    }
}
