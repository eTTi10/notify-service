package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotifyHttpPushRuntimeException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public NotifyHttpPushRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotifyHttpPushRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotifyHttpPushRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotifyHttpPushRuntimeException(final Throwable cause) {

        super(cause);
    }
}
