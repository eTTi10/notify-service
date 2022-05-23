package com.lguplus.fleta.provider.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaMessageConsumer {

    private static final String TOPIC = "MIMS_SMS";
    private static final String GROUP_ID = "MIMS_SMS_GROUP";
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @KafkaListener(id = "sms_topic" ,  topics = KafkaMessageConsumer.TOPIC, groupId = KafkaMessageConsumer.GROUP_ID)
    public void listen(String message) {
        log.debug("===> message : {}", message);
        if(message.equals("stop")){
            MessageListenerContainer sms_topic = kafkaListenerEndpointRegistry.getListenerContainer("sms_topic");
            sms_topic.stop();
        }
    }
}
