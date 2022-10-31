package com.lguplus.fleta.data.dto;

import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import static com.lguplus.fleta.data.constant.CommonResponseConstants.*;

@SuperBuilder
@Getter
public class PostPushResponseDto extends SuccessResponseDto {

    private String flag;
    private String message;

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
