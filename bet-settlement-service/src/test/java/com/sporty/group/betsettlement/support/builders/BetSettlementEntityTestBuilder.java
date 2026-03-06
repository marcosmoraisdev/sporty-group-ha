package com.sporty.group.betsettlement.support.builders;

import com.sporty.group.betsettlement.domain.SettlementResult;
import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;

import java.math.BigDecimal;

public class BetSettlementEntityTestBuilder {

    private String betId = "bet-1";
    private String userId = "user-1";
    private String eventId = "event-1";
    private String eventMarketId = "market-1";
    private String expectedWinnerId = "winner-1";
    private String actualWinnerId = "winner-1";
    private BigDecimal betAmount = new BigDecimal("10.00");
    private SettlementResult result = SettlementResult.WON;

    public static BetSettlementEntityTestBuilder builder() {
        return new BetSettlementEntityTestBuilder();
    }

    public BetSettlementEntity build() {
        return BetSettlementEntity.builder()
                .betId(betId)
                .userId(userId)
                .eventId(eventId)
                .eventMarketId(eventMarketId)
                .expectedWinnerId(expectedWinnerId)
                .actualWinnerId(actualWinnerId)
                .betAmount(betAmount)
                .result(result)
                .build();
    }
}
