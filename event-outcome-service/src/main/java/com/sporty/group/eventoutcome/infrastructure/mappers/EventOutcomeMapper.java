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

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "eventName", source = "eventName")
    @Mapping(target = "eventWinnerId", source = "eventWinnerId")
    EventOutcome toDomain(EventOutcomeRequest eventOutcomeRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventOutcomeEntity toEntity(EventOutcome eventOutcome);

    EventOutcomeMessage toMessage(EventOutcome eventOutcome);
}
