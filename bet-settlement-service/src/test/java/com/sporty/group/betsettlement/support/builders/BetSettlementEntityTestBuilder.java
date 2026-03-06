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
        BetSettlementEntity betSettlementEntity = new BetSettlementEntity();
        betSettlementEntity.setBetId(betId);
        betSettlementEntity.setUserId(userId);
        betSettlementEntity.setEventId(eventId);
        betSettlementEntity.setEventMarketId(eventMarketId);
        betSettlementEntity.setExpectedWinnerId(expectedWinnerId);
        betSettlementEntity.setActualWinnerId(actualWinnerId);
        betSettlementEntity.setBetAmount(betAmount);
        betSettlementEntity.setResult(result);

        return betSettlementEntity;
    }
}
