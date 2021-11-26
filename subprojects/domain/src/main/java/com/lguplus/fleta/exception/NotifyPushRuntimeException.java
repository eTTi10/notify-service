package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotifyPushRuntimeException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public NotifyPushRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotifyPushRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotifyPushRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotifyPushRuntimeException(final Throwable cause) {

        super(cause);
    }
}
