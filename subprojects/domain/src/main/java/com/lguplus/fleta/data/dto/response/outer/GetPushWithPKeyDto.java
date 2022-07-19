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
public class GetPushWithPKeyDto {

    private Integer pKey;
    private Integer regNo;
    private String saId;
    private String stbMac;
    private String albumId;
    private String categoryId;
    private String serviceType;
    private String msg;
    private String pushYn;
    private String resultCode;
    private String startDt;

}
