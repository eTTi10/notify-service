package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class DuplicateKeyException extends NotifyMmsRuntimeException {

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
