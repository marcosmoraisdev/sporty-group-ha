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

    public static BetSettlementTestBuilder builder() {
        return new BetSettlementTestBuilder();
    }

    public BetSettlementTestBuilder withBetId(String betId) {
        this.betId = betId;
        return this;
    }

    public BetSettlementTestBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public BetSettlementTestBuilder withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public BetSettlementTestBuilder withEventMarketId(String eventMarketId) {
        this.eventMarketId = eventMarketId;
        return this;
    }

    public BetSettlementTestBuilder withExpectedWinnerId(String expectedWinnerId) {
        this.expectedWinnerId = expectedWinnerId;
        return this;
    }

    public BetSettlementTestBuilder withActualWinnerId(String actualWinnerId) {
        this.actualWinnerId = actualWinnerId;
        return this;
    }

    public BetSettlementTestBuilder withBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
        return this;
    }

    public BetSettlementTestBuilder withResult(SettlementResult result) {
        this.result = result;
        return this;
    }

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
