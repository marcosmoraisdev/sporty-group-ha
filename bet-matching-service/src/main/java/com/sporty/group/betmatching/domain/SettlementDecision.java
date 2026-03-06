package com.sporty.group.betmatching.domain;

import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;

import java.math.BigDecimal;

public record SettlementDecision(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        BetSettlementResult result
) {
}
