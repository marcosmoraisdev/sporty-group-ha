package com.sporty.group.betsettlement.support.builders;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.domain.SettlementResult;

import java.math.BigDecimal;

public class BetSettlementTestBuilder {

    private String betId = "bet-1";
    private String userId = "user-1";
    private String eventId = "event-1";
    private String eventMarketId = "market-1";
    private String expectedWinnerId = "winner-1";
    private String actualWinnerId = "winner-1";
    private BigDecimal betAmount = new BigDecimal("10.00");
    private SettlementResult result = SettlementResult.WON;

    public BetSettlement build() {
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
}
