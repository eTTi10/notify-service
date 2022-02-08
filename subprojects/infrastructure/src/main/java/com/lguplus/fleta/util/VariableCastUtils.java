package com.lguplus.fleta.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;

/**
 * 변수 Cast Util
 * @version 1.0
 */
public class VariableCastUtils {

    private VariableCastUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<Class<?>, Class<?>> wrappingClassMap = new HashMap<>();

    static {
        wrappingClassMap.put(boolean.class, Boolean.class);
        wrappingClassMap.put(byte.class, Byte.class);
        wrappingClassMap.put(short.class, Short.class);
        wrappingClassMap.put(char.class, Character.class);
        wrappingClassMap.put(int.class, Integer.class);
        wrappingClassMap.put(long.class, Long.class);
        wrappingClassMap.put(float.class, Float.class);
        wrappingClassMap.put(double.class, Double.class);
    }

    /**
     * wrapperClass 가 있다면 찾아내고 primitiveClass 가 아니라면 그대로 반환한다.
     * @param classType 클래스 타입
     * @return Class<?>
     */
    private static Class<?> getWrapperClass(Class<?> classType) {
        if (classType.isPrimitive()) {
            return wrappingClassMap.get(classType);
        }
        return classType;
    }

    /**
     * 필드 값을 해당 클래스로 변환하여 반환한다.
     * @param value     값
     * @param classType 클래스 타입
     * @return Optional<Object>
     */
    public static Optional<Object> castValue(Object value, Class<?> classType) {
        if (value == null) {
            return Optional.empty();
        }

        Class<?> type = getWrapperClass(classType);

        if (type == String.class) {
            return Optional.of(valueOf(value));
        }

        return getBooleanType(value, type)
            .or(() -> getLongType(value, type))
            .or(() -> getIntegerType(value, type))
            .or(() -> getFloatType(value, type))
            .or(() -> getDoubleType(value, type))
            .or(() -> getLocalDateType(value, type))
            .or(() -> getLocalDateTimeType(value, type));
    }

    private static Optional<Object> getBooleanType(Object value, Class<?> type) {
        if (type == Boolean.class) {
            if (Arrays.asList("0", "1").contains(valueOf(value))) {
                return Optional.of("1".equals(valueOf(value)));
            }
            return Optional.of(Boolean.valueOf(valueOf(value)));
        }

        return Optional.empty();
    }

    private static Optional<Object> getLongType(Object value, Class<?> type) {
        if (type == Long.class) {
            if (value instanceof Number) {
                return Optional.of(((Number) value).longValue());
            }
            if (value instanceof String) {
                return Optional.of(Long.parseLong(valueOf(value)));
            }
        }

        return Optional.empty();
    }

    private static Optional<Object> getIntegerType(Object value, Class<?> type) {
        if (type == Integer.class) {
            if (value instanceof Number) {
                return Optional.of(((Number) value).intValue());
            }
            if (value instanceof String) {
                return Optional.of(Integer.parseInt(valueOf(value)));
            }
        }

        return Optional.empty();
    }

    private static Optional<Object> getFloatType(Object value, Class<?> type) {
        if (type == Float.class) {
            if (value instanceof Number) {
                return Optional.of(((Number) value).floatValue());
            }
            if (value instanceof String) {
                return Optional.of(Float.parseFloat(valueOf(value)));
            }
        }

        return Optional.empty();
    }

    private static Optional<Object> getDoubleType(Object value, Class<?> type) {
        if (type == Double.class) {
            if (value instanceof Number) {
                return Optional.of(((Number) value).doubleValue());
            }
            if (value instanceof String) {
                return Optional.of(Double.parseDouble(valueOf(value)));
            }
        }

        return Optional.empty();
    }

    private static Optional<Object> getLocalDateType(Object value, Class<?> type) {
        if (type == LocalDate.class) {
            if (value instanceof Timestamp) {
                return Optional.of(((Timestamp) value).toLocalDateTime().toLocalDate());
            }
            if (value instanceof LocalDate) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    private static Optional<Object> getLocalDateTimeType(Object value, Class<?> type) {
        if (type == LocalDateTime.class) {
            if (value instanceof Timestamp) {
                return Optional.of(((Timestamp) value).toLocalDateTime());
            }
            if (value instanceof LocalDateTime) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }
}
