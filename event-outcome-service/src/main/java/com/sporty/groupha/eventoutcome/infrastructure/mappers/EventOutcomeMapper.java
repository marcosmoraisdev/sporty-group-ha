package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = LocalDateTime.class)
public interface EventOutcomeMapper {

    default EventOutcome toDomain(EventOutcomeRequest eventOutcomeRequest) {
        String eventId = eventOutcomeRequest.eventId();
        String eventName = eventOutcomeRequest.eventName();
        String eventWinnerId = eventOutcomeRequest.eventWinnerId();

        return EventOutcome.create(eventId, eventName, eventWinnerId);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    EventOutcomeEntity toEntity(EventOutcome eventOutcome);

    EventOutcomeMessage toMessage(EventOutcome eventOutcome);
}
