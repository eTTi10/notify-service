package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class PhoneNumberErrorException extends NotifyRuntimeException {

    public PhoneNumberErrorException() {
        super();
    }

    /**
     * @param message
     */
    public PhoneNumberErrorException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public PhoneNumberErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public PhoneNumberErrorException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.SMS_SERVER_ERROR;
    }


}
