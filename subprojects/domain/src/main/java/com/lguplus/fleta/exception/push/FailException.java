package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1111
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class FailException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public FailException() {

        super();
    }

    /**
     *
     * @param message
     */
    public FailException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public FailException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public FailException(final Throwable cause) {

        super(cause);
    }
}
