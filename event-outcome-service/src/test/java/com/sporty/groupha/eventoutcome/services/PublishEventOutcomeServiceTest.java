package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.AcceptedEventOutcomeEntityMapper;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMessageMapper;
import com.sporty.groupha.eventoutcome.infrastructure.messaging.KafkaEventOutcomePublisher;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class PublishEventOutcomeServiceTest {

    @Mock
    private AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;

    @Mock
    private AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper;

    @Mock
    private EventOutcomeMessageMapper eventOutcomeMessageMapper;

    @Mock
    private KafkaEventOutcomePublisher kafkaEventOutcomePublisher;

    @InjectMocks
    private PublishEventOutcomeService publishEventOutcomeService;

    @Test
    void savesAcceptedOutcomeBeforePublishing() {
        PublishEventOutcomeCommand publishEventOutcomeCommand =
                new PublishEventOutcomeCommand("event-1", "Match A", "winner-1");
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = new AcceptedEventOutcomeEntity();
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessage("event-1", "Match A", "winner-1");
        given(acceptedEventOutcomeEntityMapper.toEntity(org.mockito.ArgumentMatchers.any()))
                .willReturn(acceptedEventOutcomeEntity);
        given(eventOutcomeMessageMapper.toMessage(org.mockito.ArgumentMatchers.any()))
                .willReturn(eventOutcomeMessage);

        publishEventOutcomeService.publish(publishEventOutcomeCommand);

        InOrder inOrder = inOrder(acceptedEventOutcomeJpaRepository, kafkaEventOutcomePublisher);
        inOrder.verify(acceptedEventOutcomeJpaRepository).save(acceptedEventOutcomeEntity);
        inOrder.verify(kafkaEventOutcomePublisher).publish(eventOutcomeMessage);
    }
}
