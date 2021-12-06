package com.lguplus.fleta.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class LatestCheckDto {
    public static final String DUPL_CODE = "DUPL";
    public static final String OVER_CODE = "OVER";
    public static final String SUCCESS_CODE = "SUCCESS";
    private String code;
}
