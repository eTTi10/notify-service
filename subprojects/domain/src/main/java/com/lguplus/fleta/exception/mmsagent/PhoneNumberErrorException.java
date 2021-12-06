package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

public class PhoneNumberErrorException extends NotifyMmsRuntimeException {


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
