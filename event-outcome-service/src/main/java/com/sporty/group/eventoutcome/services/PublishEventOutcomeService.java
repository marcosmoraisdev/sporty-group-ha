package com.sporty.group.eventoutcome.services;

import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import com.sporty.group.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.group.eventoutcome.infrastructure.persistence.EventOutcomeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishEventOutcomeService {

    private final EventOutcomeJpaRepository eventOutcomeJpaRepository;
    private final EventOutcomeMapper eventOutcomeMapper;
    private final EventPublisher<EventOutcomeMessage> eventPublisher;

    public void publish(EventOutcome eventOutcome) {
        String eventId = eventOutcome.eventId();
        log.info("Persisting and publishing event outcome for eventId={}", eventId);

        EventOutcomeEntity eventOutcomeEntity = eventOutcomeMapper.toEntity(eventOutcome);
        eventOutcomeJpaRepository.save(eventOutcomeEntity);
        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMapper.toMessage(eventOutcome);
        eventPublisher.publish(eventOutcomeMessage);

        log.info("Event outcome successfully saved and published for eventId={}", eventId);
    }
}
