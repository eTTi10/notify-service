package com.lguplus.fleta.data.type;

import java.lang.reflect.Field;

public final class CacheName {

    public static final String PUSH_STATISTICS = "PUSH_STATISTICS";

    static {
        try {
            for (final Field field : CacheName.class.getDeclaredFields()) {
                final String fieldName = field.getName();
                if (field.getType() != String.class) {
                    throw new IllegalStateException(fieldName + ": Define only cache name here.");
                }

                final String cacheName = (String) field.get(null);
                if (!field.getName().equals(cacheName)) {
                    throw new IllegalStateException(fieldName + ": Cache name should be equal to it's constant name.");
                }
            }
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private CacheName() {
    }
}
