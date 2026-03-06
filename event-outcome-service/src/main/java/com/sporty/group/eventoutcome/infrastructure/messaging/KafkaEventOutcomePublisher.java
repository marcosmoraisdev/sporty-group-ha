package com.sporty.group.eventoutcome.infrastructure.messaging;

import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventOutcomePublisher implements EventPublisher<EventOutcomeMessage> {

    private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;
    private final String topicName;

    public KafkaEventOutcomePublisher(
            KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate,
            @Value("${app.messaging.event-outcomes-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Override
    public void publish(EventOutcomeMessage eventOutcomeMessage) {
        kafkaTemplate.send(topicName, eventOutcomeMessage);
    }
}
