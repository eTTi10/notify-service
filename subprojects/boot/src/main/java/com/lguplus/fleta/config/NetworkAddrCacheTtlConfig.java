package com.lguplus.fleta.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NetworkAddrCacheTtlConfig implements CommandLineRunner {

    /**
     * Callback used to run the bean.
     * @link https://docs.aws.amazon.com/ko_kr/AmazonRDS/latest/AuroraUserGuide/Aurora.BestPractices.html
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        // TTL 값, 30초 미만 설정
        java.security.Security.setProperty("networkaddress.cache.ttl" , "1");
        // If the lookup fails, default to something like small to retry
        java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "3");

		log.info(">>> networkaddress.cache.ttl: {}, networkaddress.cache.negative.ttl: {}"
            , java.security.Security.getProperty("networkaddress.cache.ttl")
            , java.security.Security.getProperty("networkaddress.cache.negative.ttl")
        );
    }

}
