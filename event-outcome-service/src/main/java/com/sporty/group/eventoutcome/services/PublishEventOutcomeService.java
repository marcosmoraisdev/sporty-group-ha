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
        EventOutcomeEntity eventOutcomeEntity = eventOutcomeMapper.toEntity(eventOutcome);
        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMapper.toMessage(eventOutcome);

        log.info("Persisting event outcome for eventId={}", eventId);
        eventOutcomeJpaRepository.save(eventOutcomeEntity);

        log.info("Publishing event outcome message for eventId={}", eventId);
        eventPublisher.publish(eventOutcomeMessage);
    }
}
