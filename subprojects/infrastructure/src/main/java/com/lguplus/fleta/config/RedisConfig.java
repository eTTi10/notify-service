package com.lguplus.fleta.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .cacheDefaults( RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(24)) // Default TTL 값은 24시간
                        .disableCachingNullValues()
                        .prefixCacheNameWith("NY::")
                        //.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer())))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer())));
    }
}
