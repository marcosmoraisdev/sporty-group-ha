package com.sporty.groupha.eventoutcome.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.support.builders.EventOutcomeMessageTestBuilder;
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
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessageTestBuilder().build();
        EventPublisher<EventOutcomeMessage> eventPublisher =
                new KafkaEventOutcomePublisher(kafkaTemplate, "event-outcomes");

        eventPublisher.publish(eventOutcomeMessage);

        verify(kafkaTemplate).send("event-outcomes", eventOutcomeMessage);
    }
}
