package com.lguplus.fleta.data.type;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
public enum CategoryGubun {

    /**
     *
     */
    I20,

    /**
     *
     */
    I30,

    /**
     *
     */
    NSC,

    /**
     *
     */
    UNDEFINED;

    /**
     *
     */
    private static final Map<String, CategoryGubun> all = Stream.of(values())
        .collect(Collectors.toMap(Enum::name, Function.identity()));

    /**
     * @param code
     * @return
     */
    public static CategoryGubun asValue(final String code) {

        if (code == null || code.isBlank()) {
            return null;
        }

        final CategoryGubun categoryGubun = all.get(code.toUpperCase());
        if (categoryGubun == null) {
            return UNDEFINED;
        } else {
            return categoryGubun;
        }
    }
}
