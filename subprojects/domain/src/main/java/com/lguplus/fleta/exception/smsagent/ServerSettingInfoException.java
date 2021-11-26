package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class ServerSettingInfoException extends NotifySmsRuntimeException {

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
