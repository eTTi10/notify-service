package com.lguplus.fleta.config;

import com.lguplus.fleta.properties.SmsAgentProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final SmsAgentProps smsAgentProps;

    public static final String SMS_TOPIC_NAME = "MIMS_SMS";
    public static final String SMS_TOPIC_ID = "SMS_TOPIC_ID";

    @Bean
    public NewTopic CreateSmsTopic() {
        return TopicBuilder.name(SMS_TOPIC_NAME)
            .partitions(smsAgentProps.getCount())
            .replicas(1)
            .build();
    }
}
