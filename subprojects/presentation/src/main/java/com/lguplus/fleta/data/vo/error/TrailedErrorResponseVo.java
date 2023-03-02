package com.lguplus.fleta.data.vo.error;

import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "trailedErrorResponseBuilder")
public class TrailedErrorResponseVo extends ErrorResponseVo {

    @Override
    public String getFlag() {

        return super.getFlag().replaceFirst("_$", "");
    }
}
