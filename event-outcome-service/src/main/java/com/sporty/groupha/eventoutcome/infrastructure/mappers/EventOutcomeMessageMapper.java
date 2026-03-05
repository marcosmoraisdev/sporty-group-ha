package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMessageMapper {

    EventOutcomeMessage toMessage(EventOutcome eventOutcome);
}
