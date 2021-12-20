package com.lguplus.fleta.data.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CategoryType {

    /**
     *code1
    */
    LIVE("LIVE"),

    /**
     *code2
    */
    VOD("VOD"),

    /**
     *code3
    */
    CAT_MAP("CAT_MAP"),

    /**
     *code4
    */
    SCHEDULE("SCHEDULE"),

    /**
     *code5
    */
    WISH("WISH"),

    /**
    *code6
    */
    AD_H("AD_H"),

    /**
     *code7
    */
    AD_F("AD_F"),

    /**
     *code8
    */
    BOOKTV("BOOKTV"),

    /**
     *code9
    */
    ENG_PRESCH("ENG_PRESCH"),

    /**
     *code10
    */
    BRAND("BRAND"),

    /**
     *
     */
    CA_RANK("CA_RANK"),

    /**
     *
     */
    UNDEFINED("");

    private static Map<String, CategoryType> all = Stream.of(values())
            .collect(Collectors.toMap(CategoryType::toString, Function.identity()));

    private static Set<String> i20Categories = Stream.of(CAT_MAP, BOOKTV, ENG_PRESCH, BRAND)
            .map(CategoryType::toString)
            .collect(Collectors.toSet());

    private static Set<String> bestVodCategories = Stream.of(VOD, CA_RANK)
            .map(CategoryType::toString)
            .collect(Collectors.toSet());

    private static Set<String> suxmCategories = Stream.of(SCHEDULE)
            .map(CategoryType::toString)
            .collect(Collectors.toSet());

    private String code;

    CategoryType(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static CategoryType asValue(final String code) {

        if (code == null || code.isBlank()) {
            return null;
        }

        final CategoryType categoryType = all.get(code);
        if (categoryType == null) {
            return UNDEFINED;
        } else {
            return categoryType;
        }
    }

    public static boolean isI20Category(final CategoryType categoryType) {
        return isI20Category(categoryType.toString());
    }

    public static boolean isI20Category(final String categoryType) {
        return i20Categories.contains(categoryType);
    }

    public static List<String> getI20Categories() {
        return new ArrayList<String>(i20Categories);
    }

    public static boolean isBestVodCategory(final CategoryType categoryType) {
        return isBestVodCategory(categoryType.toString());
    }

    public static boolean isBestVodCategory(final String categoryType) {
        return bestVodCategories.contains(categoryType);
    }

    public static List<String> getBestVodCategories() {
        return new ArrayList<String>(bestVodCategories);
    }

    public static boolean isSuxmCategory(final CategoryType categoryType) {
        return isSuxmCategory(categoryType.toString());
    }

    public static boolean isSuxmCategory(final String categoryType) {
        return suxmCategories.contains(categoryType);
    }

    public static List<String> getSuxmCategories() {
        return new ArrayList<String>(suxmCategories);
    }
}
