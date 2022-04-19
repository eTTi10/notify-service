package com.lguplus.fleta.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class JunitTestUtils {

    public static void setValue(Object obj, String fieldName, Object value) {

        Field f = ReflectionUtils.findField(obj.getClass(), fieldName);
        if (f == null) {
            throw new RuntimeException(new NoSuchFieldException(fieldName));
        }

        ReflectionUtils.makeAccessible(f);
        ReflectionUtils.setField(f, obj, value);
    }
}
