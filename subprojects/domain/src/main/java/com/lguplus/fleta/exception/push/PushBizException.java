package com.lguplus.fleta.exception.push;

public class PushBizException extends Exception
{
    private int bizExceptionCode;
    private String bizDetailMessage;

    public PushBizException(final int code, final String message) {
        super(message);
        this.bizExceptionCode = code;
        this.bizDetailMessage = message;
    }

    public PushBizException(final int code, final String message, final Throwable cause) {
        super(message, cause);
        this.bizExceptionCode = code;
        this.bizDetailMessage = message;
    }

    public int getBizExceptionCode() {
        return bizExceptionCode;
    }

    public String getBizDetailMessage() {
        return bizDetailMessage;
    }

    public String toString() {
        return "Error: " + bizExceptionCode + " [" + bizDetailMessage + "]";
    }

    /**
     *
     * @param message
     */
    public PushBizException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public PushBizException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public PushBizException(final Throwable cause) {

        super(cause);
    }
}
