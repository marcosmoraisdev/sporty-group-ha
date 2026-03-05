package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.AcceptedEventOutcomeEntityMapper;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMessageMapper;
import com.sporty.groupha.eventoutcome.infrastructure.messaging.KafkaEventOutcomePublisher;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PublishEventOutcomeService {

    private final AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;
    private final AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper;
    private final EventOutcomeMessageMapper eventOutcomeMessageMapper;
    private final KafkaEventOutcomePublisher kafkaEventOutcomePublisher;

    public PublishEventOutcomeService(
            AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository,
            AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper,
            EventOutcomeMessageMapper eventOutcomeMessageMapper,
            KafkaEventOutcomePublisher kafkaEventOutcomePublisher
    ) {
        this.acceptedEventOutcomeJpaRepository = acceptedEventOutcomeJpaRepository;
        this.acceptedEventOutcomeEntityMapper = acceptedEventOutcomeEntityMapper;
        this.eventOutcomeMessageMapper = eventOutcomeMessageMapper;
        this.kafkaEventOutcomePublisher = kafkaEventOutcomePublisher;
    }

    public void publish(PublishEventOutcomeCommand publishEventOutcomeCommand) {
        EventOutcome eventOutcome = EventOutcome.create(
                publishEventOutcomeCommand.eventId(),
                publishEventOutcomeCommand.eventName(),
                publishEventOutcomeCommand.eventWinnerId()
        );
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = acceptedEventOutcomeEntityMapper.toEntity(eventOutcome);
        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMessageMapper.toMessage(eventOutcome);

        acceptedEventOutcomeJpaRepository.save(acceptedEventOutcomeEntity);
        kafkaEventOutcomePublisher.publish(eventOutcomeMessage);
    }
}
