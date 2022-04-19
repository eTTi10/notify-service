package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1109
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class InternalErrorException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public InternalErrorException() {

        super();
    }

    /**
     *
     * @param message
     */
    public InternalErrorException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InternalErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public InternalErrorException(final Throwable cause) {

        super(cause);
    }
}
