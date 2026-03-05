package com.sporty.groupha.betmatching.infrastructure.messaging;

import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.infrastructure.mappers.EventOutcomeMessageMapper;
import com.sporty.groupha.betmatching.services.ProcessEventOutcomeService;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventOutcomeKafkaListener {

    private final EventOutcomeMessageMapper eventOutcomeMessageMapper;
    private final ProcessEventOutcomeService processEventOutcomeService;

    public EventOutcomeKafkaListener(
            EventOutcomeMessageMapper eventOutcomeMessageMapper,
            ProcessEventOutcomeService processEventOutcomeService
    ) {
        this.eventOutcomeMessageMapper = eventOutcomeMessageMapper;
        this.processEventOutcomeService = processEventOutcomeService;
    }

    @KafkaListener(topics = "${app.messaging.event-outcomes-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(EventOutcomeMessage eventOutcomeMessage) {
        EventOutcome eventOutcome = eventOutcomeMessageMapper.toDomain(eventOutcomeMessage);

        processEventOutcomeService.process(eventOutcome);
    }
}
