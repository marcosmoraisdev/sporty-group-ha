package com.sporty.group.eventoutcome.infrastructure.messaging;

import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
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
        String eventId = eventOutcomeMessage.eventId();
        String configuredTopicName = topicName;

        log.info("Sending event outcome message to topic={} for eventId={}", configuredTopicName, eventId);
        kafkaTemplate.send(topicName, eventOutcomeMessage);
    }
}
