package com.sporty.group.betmatching.infrastructure.mappers;

import com.sporty.group.betmatching.domain.EventOutcome;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMapper {

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "eventName", source = "eventName")
    @Mapping(target = "eventWinnerId", source = "eventWinnerId")
    EventOutcome toDomain(EventOutcomeMessage eventOutcomeMessage);
}
