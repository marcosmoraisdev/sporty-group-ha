package com.sporty.groupha.betmatching.infrastructure.messaging;

import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.groupha.betmatching.services.ProcessEventOutcomeService;
import com.sporty.groupha.commonlib.messaging.EventListener;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.springframework.kafka.annotation.KafkaListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventOutcomeKafkaListener implements EventListener<EventOutcomeMessage> {

    private final EventOutcomeMapper eventOutcomeMapper;
    private final ProcessEventOutcomeService processEventOutcomeService;

    @Override
    @KafkaListener(topics = "${app.messaging.event-outcomes-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(EventOutcomeMessage eventOutcomeMessage) {
        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeMessage);

        processEventOutcomeService.process(eventOutcome);
    }
}
