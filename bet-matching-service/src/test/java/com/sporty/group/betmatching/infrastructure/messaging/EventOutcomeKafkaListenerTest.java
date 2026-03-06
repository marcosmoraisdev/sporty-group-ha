package com.sporty.group.betmatching.infrastructure.messaging;

import com.sporty.group.betmatching.domain.EventOutcome;
import com.sporty.group.betmatching.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.group.betmatching.services.ProcessEventOutcomeService;
import com.sporty.group.betmatching.support.builders.EventOutcomeMessageTestBuilder;
import com.sporty.group.betmatching.support.builders.EventOutcomeTestBuilder;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventOutcomeKafkaListenerTest {

    @Mock
    private EventOutcomeMapper eventOutcomeMapper;

    @Mock
    private ProcessEventOutcomeService processEventOutcomeService;

    @InjectMocks
    private EventOutcomeKafkaListener eventOutcomeKafkaListener;

    @Test
    void delegatesConsumedMessageToService() {
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessageTestBuilder().build();
        EventOutcome eventOutcome = new EventOutcomeTestBuilder().build();
        given(eventOutcomeMapper.toDomain(eventOutcomeMessage)).willReturn(eventOutcome);

        eventOutcomeKafkaListener.onMessage(eventOutcomeMessage);

        verify(processEventOutcomeService).process(eventOutcome);
    }
}
