package com.lguplus.fleta.data.dto.response.outer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPushDto {
    private String albumId;

    private String pushYn;

    private String resultCode;

    private String startDt;
}
