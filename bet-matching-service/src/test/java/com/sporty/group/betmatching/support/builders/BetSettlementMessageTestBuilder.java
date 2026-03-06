package com.sporty.group.betmatching.support.builders;

import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;

import java.math.BigDecimal;

public class BetSettlementMessageTestBuilder {

    private String betId = "bet-1";
    private String userId = "user-1";
    private String eventId = "event-1";
    private String eventMarketId = "market-1";
    private String expectedWinnerId = "winner-1";
    private String actualWinnerId = "winner-1";
    private BigDecimal betAmount = new BigDecimal("10.00");
    private BetSettlementResult result = BetSettlementResult.WON;

    public BetSettlementMessageTestBuilder withBetId(String betId) {
        this.betId = betId;
        return this;
    }

    public BetSettlementMessageTestBuilder withResult(BetSettlementResult result) {
        this.result = result;
        return this;
    }

    public BetSettlementMessage build() {
        return new BetSettlementMessage(
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
