package com.lguplus.fleta.exhandler;

import com.lguplus.fleta.data.dto.response.CommonErrorResponseDto;
import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;

@AllArgsConstructor
public class CustomErrorResponseConverter {

    private final Class<? extends CommonErrorResponseDto> responseClass;
    private final String builderName;

    public CommonErrorResponseDto convert(final ErrorResponseDto error) throws Throwable {

        final Object builder = responseClass.getMethod(builderName).invoke(null);
        final Class<?> builderClass = builder.getClass();
        builderClass.getMethod("flag", String.class).invoke(builder, error.getFlag());
        builderClass.getMethod("message", String.class).invoke(builder, error.getMessage());
        final Method builderMethod = builderClass.getMethod("build");
        builderMethod.setAccessible(true);
        return (CommonErrorResponseDto)builderMethod.invoke(builder);
    }
}