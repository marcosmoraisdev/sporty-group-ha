package com.sporty.group.eventoutcome.services;

import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import com.sporty.group.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.group.eventoutcome.infrastructure.persistence.EventOutcomeJpaRepository;
import com.sporty.group.eventoutcome.support.builders.EventOutcomeEntityTestBuilder;
import com.sporty.group.eventoutcome.support.builders.EventOutcomeMessageTestBuilder;
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
    private EventOutcomeJpaRepository eventOutcomeJpaRepository;

    @Mock
    private EventOutcomeMapper eventOutcomeMapper;

    @Mock
    private EventPublisher<EventOutcomeMessage> eventPublisher;

    @InjectMocks
    private PublishEventOutcomeService publishEventOutcomeService;

    @Test
    void savesAcceptedOutcomeBeforePublishing() {
        EventOutcome eventOutcome = EventOutcome.create("event-1", "Match A", "winner-1");
        EventOutcomeEntity eventOutcomeEntity = new EventOutcomeEntityTestBuilder().build();
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessageTestBuilder().build();
        given(eventOutcomeMapper.toEntity(eventOutcome))
                .willReturn(eventOutcomeEntity);
        given(eventOutcomeMapper.toMessage(eventOutcome))
                .willReturn(eventOutcomeMessage);

        publishEventOutcomeService.publish(eventOutcome);

        InOrder inOrder = inOrder(eventOutcomeJpaRepository, eventPublisher);
        inOrder.verify(eventOutcomeJpaRepository).save(eventOutcomeEntity);
        inOrder.verify(eventPublisher).publish(eventOutcomeMessage);
    }
}
