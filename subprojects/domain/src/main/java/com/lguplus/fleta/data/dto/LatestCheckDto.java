package com.lguplus.fleta.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

@Data
@SuperBuilder
public class LatestCheckDto {
    public static final String DUPL_CODE = "DUPL";
    public static final String OVER_CODE = "OVER";
    public static final String SUCCESS_CODE = "SUCCESS";
    @Value("")
    private int maxCount;
    private String code;
}
