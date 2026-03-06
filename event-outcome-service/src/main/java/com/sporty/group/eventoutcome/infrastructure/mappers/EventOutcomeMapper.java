package com.sporty.group.eventoutcome.infrastructure.mappers;

import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMapper {

    default EventOutcome toDomain(EventOutcomeRequest eventOutcomeRequest) {
        String eventId = eventOutcomeRequest.eventId();
        String eventName = eventOutcomeRequest.eventName();
        String eventWinnerId = eventOutcomeRequest.eventWinnerId();

        return EventOutcome.create(eventId, eventName, eventWinnerId);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventOutcomeEntity toEntity(EventOutcome eventOutcome);

    EventOutcomeMessage toMessage(EventOutcome eventOutcome);
}
