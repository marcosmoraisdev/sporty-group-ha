package com.sporty.group.betmatching.support.builders;

import com.sporty.group.betmatching.domain.Bet;

import java.math.BigDecimal;

public class BetTestBuilder {

    private String betId = "bet-1";
    private String userId = "user-1";
    private String eventId = "event-1";
    private String eventMarketId = "market-1";
    private String eventWinnerId = "winner-1";
    private BigDecimal betAmount = new BigDecimal("10.00");

    public static BetTestBuilder builder() {
        return new BetTestBuilder();
    }

    public BetTestBuilder withBetId(String betId) {
        this.betId = betId;
        return this;
    }

    public BetTestBuilder withEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
        return this;
    }

    public Bet build() {
        return new Bet(betId, userId, eventId, eventMarketId, eventWinnerId, betAmount);
    }
}
