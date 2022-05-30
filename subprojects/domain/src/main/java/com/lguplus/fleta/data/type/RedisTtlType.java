package com.lguplus.fleta.data.type;

public enum RedisTtlType {
    TTL_1(1),
    TTL_2(2),
    TTL_15(15),
    TTL_30(30),
    TTL_60(60),
    TTL_H12(12),
    TTL_D1(1),
    TTL_D7(7);

    private final long value;

    RedisTtlType(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
