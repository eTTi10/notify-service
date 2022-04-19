package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class DuplicateKeyException extends NotifyRuntimeException {

    @Override
    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

    public DuplicateKeyException() {
        super();
    }

    /**
     *
     * @param message
     */
    public DuplicateKeyException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DuplicateKeyException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DuplicateKeyException(final Throwable cause) {

        super(cause);
    }

}
