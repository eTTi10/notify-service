package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 9999
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class MmsRuntimeException extends NotifyRuntimeException {

    /**
     *
     */
    public MmsRuntimeException() {

        super();
    }


    /**
     * @param message
     */
    public MmsRuntimeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MmsRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public MmsRuntimeException(final Throwable cause) {

        super(cause);
    }

    @Override
    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }
}