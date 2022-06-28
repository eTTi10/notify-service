package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class SmsAgentEtcException extends NotifyRuntimeException {

    public SmsAgentEtcException() {
        super();
    }

    /**
     * @param message
     */
    public SmsAgentEtcException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SmsAgentEtcException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public SmsAgentEtcException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

}
