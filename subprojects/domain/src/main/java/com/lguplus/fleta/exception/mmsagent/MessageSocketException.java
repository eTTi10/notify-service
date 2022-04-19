package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class MessageSocketException extends NotifyRuntimeException {

    @Override
    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

    public MessageSocketException() {
        super();
    }

    /**
     *
     * @param message
     */
    public MessageSocketException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MessageSocketException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public MessageSocketException(final Throwable cause) {

        super(cause);
    }

}
