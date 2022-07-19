package com.lguplus.fleta.exception.musicshow;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

public class MusicShowException extends NotifyRuntimeException {

    private static final long serialVersionUID = 2533173390629509755L;

    String flag;
    String message;

    @Override
    public InnerResponseCodeType getInnerResponseCodeType() {
        return null;
    }

    public MusicShowException() {
    }

    public MusicShowException(String message) {
        this.message = message;
    }

    public MusicShowException(String flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
