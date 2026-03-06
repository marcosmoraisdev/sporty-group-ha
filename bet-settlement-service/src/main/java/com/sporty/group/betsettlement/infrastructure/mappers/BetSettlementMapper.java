package com.sporty.group.betsettlement.infrastructure.mappers;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.domain.SettlementResult;
import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    @Mapping(target = "betId", source = "betId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "eventMarketId", source = "eventMarketId")
    @Mapping(target = "expectedWinnerId", source = "expectedWinnerId")
    @Mapping(target = "actualWinnerId", source = "actualWinnerId")
    @Mapping(target = "betAmount", source = "betAmount")
    @Mapping(target = "result", source = "result")
    BetSettlementValues toDomainValues(BetSettlementMessage betSettlementMessage);

    default BetSettlement toDomain(BetSettlementMessage betSettlementMessage) {
        BetSettlementValues betSettlementValues = toDomainValues(betSettlementMessage);
        String betId = betSettlementValues.betId();
        String userId = betSettlementValues.userId();
        String eventId = betSettlementValues.eventId();
        String eventMarketId = betSettlementValues.eventMarketId();
        String expectedWinnerId = betSettlementValues.expectedWinnerId();
        String actualWinnerId = betSettlementValues.actualWinnerId();
        java.math.BigDecimal betAmount = betSettlementValues.betAmount();
        SettlementResult result = betSettlementValues.result();

        return BetSettlement.create(
                betId,
                userId,
                eventId,
                eventMarketId,
                expectedWinnerId,
                actualWinnerId,
                betAmount,
                result
        );
    }

    @Mapping(target = "id", ignore = true)
    BetSettlementEntity toEntity(BetSettlement betSettlement);

    default SettlementResult map(BetSettlementResult betSettlementResult) {
        if (betSettlementResult == null) {
            return null;
        }

        String settlementResultName = betSettlementResult.name();
        return SettlementResult.valueOf(settlementResultName);
    }

    record BetSettlementValues(
            String betId,
            String userId,
            String eventId,
            String eventMarketId,
            String expectedWinnerId,
            String actualWinnerId,
            java.math.BigDecimal betAmount,
            SettlementResult result
    ) {
    }
}
