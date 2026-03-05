package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = java.time.LocalDateTime.class)
public interface AcceptedEventOutcomeEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    AcceptedEventOutcomeEntity toEntity(EventOutcome eventOutcome);
}
