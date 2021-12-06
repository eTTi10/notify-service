package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

/**
 * 5200: 서버 설정 정보 오류
 */
public class ServerSettingInfoException extends NotifyMmsRuntimeException {

    public ServerSettingInfoException() {
        super();
    }

    /**
     *
     * @param message
     */
    public ServerSettingInfoException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ServerSettingInfoException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ServerSettingInfoException(final Throwable cause) {

        super(cause);
    }

}
