package com.lguplus.fleta.data.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class LatestCheckDto {
    public static final String DUPL_CODE = "DUPL";
    public static final String OVER_CODE = "OVER";
    public static final String SUCCESS_CODE = "SUCCESS";
    private String code;
}
