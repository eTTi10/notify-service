package com.lguplus.fleta.data.dto.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SmartStartItemRequestDto extends CommonRequestDto{

    //private String callByScheduler;

    private String config_pannel_code;

    public String getConfig_pannel_code() {
        return (config_pannel_code == null) ? "" : config_pannel_code;
    }
}
