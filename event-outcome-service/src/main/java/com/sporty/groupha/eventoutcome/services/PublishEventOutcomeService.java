package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishEventOutcomeService {

    private final AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;
    private final EventOutcomeMapper eventOutcomeMapper;
    private final EventPublisher<EventOutcomeMessage> eventPublisher;

    public void publish(EventOutcome eventOutcome) {
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = eventOutcomeMapper.toEntity(eventOutcome);
        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMapper.toMessage(eventOutcome);

        acceptedEventOutcomeJpaRepository.save(acceptedEventOutcomeEntity);
        eventPublisher.publish(eventOutcomeMessage);
    }
}
