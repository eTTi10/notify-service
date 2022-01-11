package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1113
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotExistRegistIdException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public NotExistRegistIdException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotExistRegistIdException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotExistRegistIdException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotExistRegistIdException(final Throwable cause) {

        super(cause);
    }
}
