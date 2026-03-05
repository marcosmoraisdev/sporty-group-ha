package com.sporty.groupha.eventoutcome.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventOutcomePublisher {

    private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;
    private final String topicName;

    public KafkaEventOutcomePublisher(
            KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate,
            @Value("${app.messaging.event-outcomes-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publish(EventOutcomeMessage eventOutcomeMessage) {
        kafkaTemplate.send(topicName, eventOutcomeMessage);
    }
}
