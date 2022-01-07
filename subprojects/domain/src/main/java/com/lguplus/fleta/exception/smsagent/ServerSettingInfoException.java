package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class ServerSettingInfoException extends NotifyRuntimeException {


    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

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
