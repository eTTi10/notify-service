package com.lguplus.fleta.data.dto;

import com.lguplus.fleta.util.CommonUtil;
import lombok.Builder;

@Builder
public class PostPushResponseDto {

    public static final String COLSEP = "!^"; // 열 분리자 -> constants로 변경 필요

    private String flag;
    private String message;

    public String getFlag() {
        return CommonUtil.checkNullStr(flag);
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return CommonUtil.checkNullStr(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFlag());
        sb.append(COLSEP);
        sb.append(getMessage());
        return sb.toString();
    }
}
