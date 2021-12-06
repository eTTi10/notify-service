package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class MmsServiceException extends NotifyMmsRuntimeException {

    public MmsServiceException() {
        super();
    }

    /**
     *
     * @param message
     */
    public MmsServiceException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MmsServiceException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public MmsServiceException(final Throwable cause) {

        super(cause);
    }

}
