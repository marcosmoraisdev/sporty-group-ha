package com.sporty.groupha.betmatching.infrastructure.mappers;

import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMessageMapper {

    EventOutcome toDomain(EventOutcomeMessage eventOutcomeMessage);
}
