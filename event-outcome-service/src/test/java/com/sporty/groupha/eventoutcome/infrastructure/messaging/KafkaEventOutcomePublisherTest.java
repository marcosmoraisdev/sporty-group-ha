package com.sporty.groupha.eventoutcome.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventOutcomePublisherTest {

    @Mock
    private KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;

    @Test
    void sendsEventOutcomeMessageToConfiguredTopic() {
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessage("event-1", "Match A", "winner-1");
        KafkaEventOutcomePublisher kafkaEventOutcomePublisher =
                new KafkaEventOutcomePublisher(kafkaTemplate, "event-outcomes");

        kafkaEventOutcomePublisher.publish(eventOutcomeMessage);

        verify(kafkaTemplate).send("event-outcomes", eventOutcomeMessage);
    }
}
