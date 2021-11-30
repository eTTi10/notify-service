package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifySmsRuntimeException;

public class PhoneNumberErrorException extends NotifySmsRuntimeException {


    public PhoneNumberErrorException() {
        super();
    }

    /**
     *
     * @param message
     */
    public PhoneNumberErrorException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public PhoneNumberErrorException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public PhoneNumberErrorException(final Throwable cause) {

        super(cause);
    }


}
