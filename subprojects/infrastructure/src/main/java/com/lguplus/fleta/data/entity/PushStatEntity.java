package com.lguplus.fleta.data.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Builder
@RedisHash("pushstat")
public class PushStatEntity implements Serializable {
    @Id
    private String cacheKeyId;

    //Data
    private String serviceId;
    private long interval;
    private long processCount;
    private long timestamp;
}
