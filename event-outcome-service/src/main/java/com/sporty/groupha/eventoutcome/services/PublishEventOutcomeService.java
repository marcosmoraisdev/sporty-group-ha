package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.AcceptedEventOutcomeEntityMapper;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PublishEventOutcomeService {

    private final AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;
    private final AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper;

    public PublishEventOutcomeService(
            AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository,
            AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper
    ) {
        this.acceptedEventOutcomeJpaRepository = acceptedEventOutcomeJpaRepository;
        this.acceptedEventOutcomeEntityMapper = acceptedEventOutcomeEntityMapper;
    }

    public void publish(PublishEventOutcomeCommand publishEventOutcomeCommand) {
        EventOutcome eventOutcome = EventOutcome.create(
                publishEventOutcomeCommand.eventId(),
                publishEventOutcomeCommand.eventName(),
                publishEventOutcomeCommand.eventWinnerId()
        );
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = acceptedEventOutcomeEntityMapper.toEntity(eventOutcome);

        acceptedEventOutcomeJpaRepository.save(acceptedEventOutcomeEntity);
    }
}
