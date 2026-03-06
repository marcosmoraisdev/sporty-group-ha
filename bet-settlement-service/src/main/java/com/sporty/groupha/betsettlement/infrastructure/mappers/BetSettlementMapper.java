package com.sporty.groupha.betsettlement.infrastructure.mappers;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.domain.SettlementResult;
import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    default BetSettlement toDomain(BetSettlementMessage betSettlementMessage) {
        String betId = betSettlementMessage.betId();
        String userId = betSettlementMessage.userId();
        String eventId = betSettlementMessage.eventId();
        String eventMarketId = betSettlementMessage.eventMarketId();
        String expectedWinnerId = betSettlementMessage.expectedWinnerId();
        String actualWinnerId = betSettlementMessage.actualWinnerId();
        BigDecimal betAmount = betSettlementMessage.betAmount();
        String settlementResultName = betSettlementMessage.result().name();
        SettlementResult settlementResult = SettlementResult.valueOf(settlementResultName);

        return BetSettlement.create(
                betId,
                userId,
                eventId,
                eventMarketId,
                expectedWinnerId,
                actualWinnerId,
                betAmount,
                settlementResult
        );
    }

    @Mapping(target = "id", ignore = true)
    BetSettlementEntity toEntity(BetSettlement betSettlement);
}
