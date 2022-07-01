package com.lguplus.fleta.data.type;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
public enum CarrierType {

    /**
     *
     */
    LGU("L"),

    /**
     *
     */
    KT("K"),

    /**
     *
     */
    SKT("S"),

    /**
     *
     */
    ETC("E"),

    /**
     *
     */
    UNDEFINED("");

    /**
     *
     */
    private static final Map<String, CarrierType> all = Stream.of(values())
        .collect(Collectors.toMap(CarrierType::toString, Function.identity()));

    /**
     *
     */
    private final String code;

    /**
     *
     */
    CarrierType(final String code) {

        this.code = code;
    }

    /**
     * @param code
     * @return
     */
    public static CarrierType asValue(final String code) {

        if (code == null || code.isBlank()) {
            return null;
        }

        final CarrierType carrierType = all.get(code);
        if (carrierType == null) {
            return UNDEFINED;
        } else {
            return carrierType;
        }
    }

    /**
     *
     */
    @Override
    public String toString() {

        return code;
    }
}
