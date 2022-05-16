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

    private final String code;
    /**
     *
     */
    public SmsAgentCustomException(final String code) {

        super();
        this.code = code;
    }

    /**
     *
     * @param message
     */
    public SmsAgentCustomException(final String code, final String message) {

        super(message);
        this.code = code;
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SmsAgentCustomException(final String code, final String message, final Throwable cause) {

        super(message, cause);
        this.code = code;
    }

    /**
     *
     * @param cause
     */
    public SmsAgentCustomException(final String code, final Throwable cause) {

        super(cause);
        this.code = code;
    }
}
