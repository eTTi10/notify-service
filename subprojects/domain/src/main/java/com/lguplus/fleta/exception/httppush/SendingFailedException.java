package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1130
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SendingFailedException extends NotifyRuntimeException {

    /**
     *
     */
    public SendingFailedException() {

        super();
    }

    /**
     * @param message
     */
    public SendingFailedException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SendingFailedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public SendingFailedException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }
}
