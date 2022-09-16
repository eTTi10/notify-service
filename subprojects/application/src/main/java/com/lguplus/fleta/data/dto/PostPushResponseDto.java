package com.lguplus.fleta.data.dto;

import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PostPushResponseDto extends SuccessResponseDto {

    private String flag;
    private String message;
    private static final String SUCCESS_FLAG = "0000";
    private static final String SUCCESS_MESSAGE = "성공";

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccessFlag() {
        setFlag(SUCCESS_FLAG);
        setMessage(SUCCESS_MESSAGE);
    }

}
