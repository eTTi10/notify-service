package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class NoHttpsException extends NotifyMmsRuntimeException {

    public NoHttpsException() {
        super();
    }

    /**
     *
     * @param message
     */
    public NoHttpsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoHttpsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoHttpsException(final Throwable cause) {

        super(cause);
    }

}
