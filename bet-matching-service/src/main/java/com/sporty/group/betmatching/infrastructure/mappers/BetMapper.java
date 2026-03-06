package com.sporty.group.betmatching.infrastructure.mappers;

import com.sporty.group.betmatching.domain.Bet;
import com.sporty.group.betmatching.infrastructure.entity.BetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BetMapper {

    Bet toDomain(BetEntity betEntity);
}
