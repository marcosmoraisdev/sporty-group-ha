package com.sporty.groupha.betmatching.infrastructure.mappers;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetMapper {

    @Mapping(target = "tleAgainst", ignore = true)
    @Mapping(target = "betId", source = "betId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "eventMarketId", source = "eventMarketId")
    @Mapping(target = "eventWinnerId", source = "eventWinnerId")
    @Mapping(target = "betAmount", source = "betAmount")
    Bet toDomain(BetEntity betEntity);
}
