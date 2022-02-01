package com.lguplus.fleta.exception;

public class ServiceException extends RuntimeException {

    public ServiceException(Throwable e) {
        super(e);
    }

    public ServiceException(String errorMessage) {
        super(errorMessage);
    }

    public ServiceException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

}
