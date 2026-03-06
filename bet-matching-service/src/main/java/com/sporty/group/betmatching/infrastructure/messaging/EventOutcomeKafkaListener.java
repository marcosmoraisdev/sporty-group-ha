package com.sporty.group.betmatching.infrastructure.messaging;

import com.sporty.group.betmatching.domain.EventOutcome;
import com.sporty.group.betmatching.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.group.betmatching.services.ProcessEventOutcomeService;
import com.sporty.group.commonlib.messaging.EventListener;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import org.springframework.kafka.annotation.KafkaListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOutcomeKafkaListener implements EventListener<EventOutcomeMessage> {

    private final EventOutcomeMapper eventOutcomeMapper;
    private final ProcessEventOutcomeService processEventOutcomeService;

    @Override
    @KafkaListener(topics = "${app.messaging.event-outcomes-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(EventOutcomeMessage eventOutcomeMessage) {
        String eventId = eventOutcomeMessage.eventId();
        log.info("Consumed event outcome message for eventId={}", eventId);

        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeMessage);

        processEventOutcomeService.process(eventOutcome);
    }
}
