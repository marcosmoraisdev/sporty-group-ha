package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import com.sporty.groupha.eventoutcome.support.builders.EventOutcomeEntityTestBuilder;
import com.sporty.groupha.eventoutcome.support.builders.EventOutcomeMessageTestBuilder;
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
    private EventOutcomeMapper eventOutcomeMapper;

    @Mock
    private EventPublisher<EventOutcomeMessage> eventPublisher;

    @InjectMocks
    private PublishEventOutcomeService publishEventOutcomeService;

    @Test
    void savesAcceptedOutcomeBeforePublishing() {
        EventOutcome eventOutcome = EventOutcome.create("event-1", "Match A", "winner-1");
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = new EventOutcomeEntityTestBuilder().build();
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessageTestBuilder().build();
        given(eventOutcomeMapper.toEntity(eventOutcome))
                .willReturn(acceptedEventOutcomeEntity);
        given(eventOutcomeMapper.toMessage(eventOutcome))
                .willReturn(eventOutcomeMessage);

        publishEventOutcomeService.publish(eventOutcome);

        InOrder inOrder = inOrder(acceptedEventOutcomeJpaRepository, eventPublisher);
        inOrder.verify(acceptedEventOutcomeJpaRepository).save(acceptedEventOutcomeEntity);
        inOrder.verify(eventPublisher).publish(eventOutcomeMessage);
    }
}
