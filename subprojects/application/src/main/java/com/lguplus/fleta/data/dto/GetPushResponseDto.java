package com.lguplus.fleta.data.dto;

import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import static com.lguplus.fleta.data.constant.CommonResponseConstants.*;

@SuperBuilder
@Getter
public class GetPushResponseDto extends SuccessResponseDto {

    /*
     * 푸시등록여부
     * Y:알림등록
     * N:알림해제
     */
    private String push_yn = "";

    /**
     * 공연시작일시
     * (yyyyMMddHHmm)
     */
    private String start_dt = "";

    private String flag = "";
    private String message = "";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFlag());
        sb.append(COLSEP);
        sb.append(getMessage());
        sb.append(COLSEP);
        sb.append(push_yn);
        sb.append(COLSEP);
        sb.append(start_dt);

        return sb.toString();
    }


    public static GetPushResponseDto create(GetPushDto dto) {

        if (dto == null) {
            return GetPushResponseDto.builder()
                .push_yn("N")
                .flag(SUCCESS_FLAG)
                .message(SUCCESS_MESSAGE)
                .build();
        }

        return GetPushResponseDto.builder()
            .push_yn(dto.getPushYn())
            .start_dt(dto.getStartDt())
            .flag(SUCCESS_FLAG)
            .message(SUCCESS_MESSAGE)
            .build();
    }
}
