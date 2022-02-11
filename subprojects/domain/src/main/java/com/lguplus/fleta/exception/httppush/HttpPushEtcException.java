package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1108
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class HttpPushEtcException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public HttpPushEtcException() {

        super();
    }

    /**
     *
     * @param message
     */
    public HttpPushEtcException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public HttpPushEtcException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public HttpPushEtcException(final Throwable cause) {

        super(cause);
    }
}
