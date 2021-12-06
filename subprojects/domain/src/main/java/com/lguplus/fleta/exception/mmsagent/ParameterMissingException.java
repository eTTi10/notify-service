package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * 1506, 해당 코드에 존재하는 메시지가 없음
 * 데이터 단건을 예상했지만 데이텨ㅓ가 출력되지 않은 경우
 */
public class ParameterMissingException extends NotifyMmsRuntimeException {

    public ParameterMissingException() {
        super();
    }

    /**
     *
     * @param message
     */
    public ParameterMissingException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ParameterMissingException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ParameterMissingException(final Throwable cause) {

        super(cause);
    }

}
