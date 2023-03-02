package com.lguplus.fleta.exception;

public class MmsParameterMissingException extends ParameterMissingException{

    /**
     *
     */
    public MmsParameterMissingException() {

        super();
    }

    /**
     * @param message
     */
    public MmsParameterMissingException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MmsParameterMissingException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public MmsParameterMissingException(final Throwable cause) {

        super(cause);
    }

}
