package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeApiMapper {

    PublishEventOutcomeCommand toCommand(EventOutcomeRequest eventOutcomeRequest);
}
