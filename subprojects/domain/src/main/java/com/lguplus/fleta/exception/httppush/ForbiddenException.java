package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1106
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ForbiddenException extends NotifyRuntimeException {

    /**
     *
     */
    public ForbiddenException() {

        super();
    }

    /**
     * @param message
     */
    public ForbiddenException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ForbiddenException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ForbiddenException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }
}
