package com.lguplus.fleta.exception.push;


import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1116
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class TimeoutException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public TimeoutException() {

        super();
    }

    /**
     *
     * @param message
     */
    public TimeoutException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public TimeoutException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public TimeoutException(final Throwable cause) {

        super(cause);
    }
}
