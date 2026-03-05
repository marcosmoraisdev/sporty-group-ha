package com.sporty.groupha.betmatching.domain;

import java.math.BigDecimal;

public record SettlementDecision(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        SettlementResult result
) {
}
