package com.lguplus.fleta.provider.kafka;

import com.lguplus.fleta.config.KafkaConfig;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SmsKafkaListener {

    private static final String GROUP_ID = "MIMS_SMS_GROUP";
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private MessageListenerContainer smsTopicContainer;

    @KafkaListener(id = KafkaConfig.SMS_TOPIC_ID, topics = KafkaConfig.SMS_TOPIC_NAME, groupId = SmsKafkaListener.GROUP_ID, autoStartup = "false")
//        @KafkaListener(id = KafkaConfig.SMS_TOPIC_ID, topics = KafkaConfig.SMS_TOPIC_NAME, groupId = SmsKafkaListener.GROUP_ID)
    public void listen(String message) {
        log.debug("===> message : {}", message);
        if (message.equals("stop") && smsTopicContainer != null) {
            smsTopicContainer.stop();
        }
    }
}
