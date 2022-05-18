package com.lguplus.fleta.exhandler;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class CustomErrorResponseConverter {

    private final Class<? extends CommonResponseDto> responseClass;
    private final String builderName;

    public CommonResponseDto convert(final ErrorResponseDto error)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Object builder = responseClass.getMethod(builderName).invoke(null);
        final Class<?> builderClass = builder.getClass();
        builderClass.getMethod("flag", String.class).invoke(builder, error.getFlag());
        builderClass.getMethod("message", String.class).invoke(builder, error.getMessage());
        final Method builderMethod = builderClass.getMethod("build");
        ReflectionUtils.makeAccessible(builderMethod);
        return (CommonResponseDto)builderMethod.invoke(builder);
    }
}