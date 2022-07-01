package com.lguplus.fleta.data.type;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
public enum DeviceInfo {

    /**
     *
     */
    PHONE,

    /**
     *
     */
    PAD,

    /**
     *
     */
    PC,

    /**
     *
     */
    TV,

    /**
     *
     */
    STB,

    /**
     *
     */
    UNDEFINED;

    /**
     *
     */
    private static final Map<String, DeviceInfo> all = Stream.of(values())
        .collect(Collectors.toMap(Enum::name, Function.identity()));

    /**
     * @param code
     * @return
     */
    public static DeviceInfo asValue(final String code) {

        if (code == null || code.isBlank()) {
            return null;
        }

        final DeviceInfo deviceInfo = all.get(code);
        if (deviceInfo == null) {
            return UNDEFINED;
        } else {
            return deviceInfo;
        }
    }

    /**
     *
     */
    @Override
    public String toString() {

        if (this == UNDEFINED) {
            return "";
        } else {
            return super.toString();
        }
    }
}
