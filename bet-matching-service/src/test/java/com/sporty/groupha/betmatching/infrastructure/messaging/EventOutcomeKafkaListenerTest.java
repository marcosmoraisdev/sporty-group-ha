package com.sporty.groupha.betmatching.infrastructure.messaging;

import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.groupha.betmatching.services.ProcessEventOutcomeService;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
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
        EventOutcomeMessage eventOutcomeMessage = new EventOutcomeMessage("event-1", "Match A", "winner-1");
        EventOutcome eventOutcome = new EventOutcome("event-1", "Match A", "winner-1");
        given(eventOutcomeMapper.toDomain(eventOutcomeMessage)).willReturn(eventOutcome);

        eventOutcomeKafkaListener.onMessage(eventOutcomeMessage);

        verify(processEventOutcomeService).process(eventOutcome);
    }
}
