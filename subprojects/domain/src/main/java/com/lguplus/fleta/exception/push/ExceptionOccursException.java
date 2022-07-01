package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1114
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ExceptionOccursException extends NotifyRuntimeException {

    /**
     *
     */
    public ExceptionOccursException() {

        super();
    }

    /**
     * @param message
     */
    public ExceptionOccursException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ExceptionOccursException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ExceptionOccursException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.PUSH_SERVER_ERROR;
    }
}
