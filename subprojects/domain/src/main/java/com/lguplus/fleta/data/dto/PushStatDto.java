package com.lguplus.fleta.data.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class PushStatDto implements Serializable {
    @Id
    private String serviceId;

    private long measureIntervalMillis;
    private long measurePushCount;
    private long measureStartMillis;

    public void setMeasurePushCount(long measurePushCount) {
        this.measurePushCount = measurePushCount;
    }

    public void setMeasureStartMillis(long measureStartMillis) {
        this.measureStartMillis = measureStartMillis;
    }

    public boolean isIntervalOver() {
        return (getIntervalTimeGap() >= measureIntervalMillis);
    }

    public long getIntervalTimeGap() {
        return System.currentTimeMillis() - this.getMeasureStartMillis();
    }
}