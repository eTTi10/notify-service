package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotifyMmsRuntimeException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

    /**
     *
     */
    public NotifyMmsRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotifyMmsRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotifyMmsRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotifyMmsRuntimeException(final Throwable cause) {

        super(cause);
    }
}
