package com.lguplus.fleta.data.type.response;

import java.util.List;

/**
 * HTTP API 표준 응답 Data 형식
 * @version 0.1.0
 */
public enum InnerResponseDataType {
    SINGLE,
    LIST;

    public static InnerResponseDataType of(Object data) {
        return isListType(data) ? InnerResponseDataType.LIST : InnerResponseDataType.SINGLE;
    }

    public static int sizeOf(Object data) {
        return isListType(data) ? ((List<?>) data).size() : (data != null ? 1 : 0);
    }

    private static boolean isListType(Object data) {
        return data instanceof List;
    }
}
