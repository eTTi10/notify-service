package com.lguplus.fleta.data.dto.response.outer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPushDto {
    private String albumId;

    private String pushYn;

    private String resultCode;

    private String startDt;
}
