package com.sporty.groupha.betmatching.infrastructure.mappers;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetEntityMapper {

    default Bet toDomain(BetEntity betEntity) {
        return new Bet(
                betEntity.getBetId(),
                betEntity.getUserId(),
                betEntity.getEventId(),
                betEntity.getEventMarketId(),
                betEntity.getEventWinnerId(),
                betEntity.getBetAmount()
        );
    }
}
