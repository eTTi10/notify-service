package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsAgentCustomException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }

    private String code;

    private String message;
    /**
     *
     */
    public SmsAgentCustomException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SmsAgentCustomException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SmsAgentCustomException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SmsAgentCustomException(final Throwable cause) {

        super(cause);
    }
}
