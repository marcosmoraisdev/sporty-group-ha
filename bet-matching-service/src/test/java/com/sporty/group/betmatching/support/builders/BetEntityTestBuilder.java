package com.sporty.group.betmatching.support.builders;

import com.sporty.group.betmatching.infrastructure.entity.BetEntity;

import java.math.BigDecimal;

public class BetEntityTestBuilder {

    private String betId = "bet-1";
    private String userId = "user-1";
    private String eventId = "event-1";
    private String eventMarketId = "market-1";
    private String eventWinnerId = "winner-1";
    private BigDecimal betAmount = new BigDecimal("10.00");

    public static BetEntityTestBuilder builder() {
        return new BetEntityTestBuilder();
    }

    public BetEntityTestBuilder withBetId(String betId) {
        this.betId = betId;
        return this;
    }

    public BetEntity build() {
        return BetEntity.builder()
                .betId(betId)
                .userId(userId)
                .eventId(eventId)
                .eventMarketId(eventMarketId)
                .eventWinnerId(eventWinnerId)
                .betAmount(betAmount)
                .build();
    }
}
