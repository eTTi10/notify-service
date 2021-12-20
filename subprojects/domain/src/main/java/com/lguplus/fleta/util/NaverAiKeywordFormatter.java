package com.lguplus.fleta.util;

public final class NaverAiKeywordFormatter {

    private NaverAiKeywordFormatter() {

        // Do nothing.
    }

    public static String format(final String keyword) {

        if (keyword == null) {
            return keyword;
        } else {
            final String regexp = "\\[(.*?)\\]|\\((.*?)\\)|\\<(.*?)\\>";
            return keyword.replaceAll(regexp, "");
        }
    }
}
