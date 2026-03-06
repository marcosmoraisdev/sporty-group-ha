package com.sporty.group.eventoutcome.services;

import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import com.sporty.group.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.group.eventoutcome.infrastructure.persistence.EventOutcomeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishEventOutcomeService {

    private final EventOutcomeJpaRepository eventOutcomeJpaRepository;
    private final EventOutcomeMapper eventOutcomeMapper;
    private final EventPublisher<EventOutcomeMessage> eventPublisher;

    public void publish(EventOutcome eventOutcome) {
        EventOutcomeEntity eventOutcomeEntity = eventOutcomeMapper.toEntity(eventOutcome);
        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMapper.toMessage(eventOutcome);

        eventOutcomeJpaRepository.save(eventOutcomeEntity);
        eventPublisher.publish(eventOutcomeMessage);
    }
}
