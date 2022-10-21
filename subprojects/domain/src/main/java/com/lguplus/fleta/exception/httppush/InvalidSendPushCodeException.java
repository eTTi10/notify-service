package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 9990.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class InvalidSendPushCodeException extends NotifyRuntimeException {

    /**
     *
     */
    public InvalidSendPushCodeException() {

        super();
    }

    /**
     * @param message
     */
    public InvalidSendPushCodeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidSendPushCodeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public InvalidSendPushCodeException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }
}
