package com.lguplus.fleta.provider.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
//@Component
public class KafkaMessageProducer {

    private static final String TOPIC = "MIMS_SMS";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        log.debug("===> message : {}", message);
        this.kafkaTemplate.send(TOPIC, "", message);
    }

    public void send(String message, String key) {
        log.debug("===> message : {} : {}", message, key);
        this.kafkaTemplate.send(TOPIC, key, message);
    }
}
