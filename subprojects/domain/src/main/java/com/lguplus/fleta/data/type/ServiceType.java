package com.lguplus.fleta.data.type;

public enum ServiceType {
    MUSIC_SHOW("C");

    private final String CODE;

    ServiceType(String value) {
        this.CODE = value;
    }

    public String getCode() {
        return CODE;
    }
}
