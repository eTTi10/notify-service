package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * 1506, 해당 코드에 존재하는 메시지가 없음
 * 데이터 단건을 예상했지만 데이텨ㅓ가 출력되지 않은 경우
 */
public class NotFoundMsgException extends NotifyMmsRuntimeException {

    public NotFoundMsgException() {
        super();
    }

    /**
     *
     * @param message
     */
    public NotFoundMsgException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotFoundMsgException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotFoundMsgException(final Throwable cause) {

        super(cause);
    }

}
