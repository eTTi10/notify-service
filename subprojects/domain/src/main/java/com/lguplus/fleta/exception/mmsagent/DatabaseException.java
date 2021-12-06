package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class DatabaseException extends NotifyMmsRuntimeException {

    public DatabaseException() {
        super();
    }

    /**
     *
     * @param message
     */
    public DatabaseException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DatabaseException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DatabaseException(final Throwable cause) {

        super(cause);
    }

}
