package com.sporty.groupha.betmatching.domain;

import java.math.BigDecimal;

public record Bet(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String eventWinnerId,
        BigDecimal betAmount
) {

    public SettlementDecision settleAgainst(EventOutcome eventOutcome) {
        SettlementResult settlementResult = eventWinnerId.equals(eventOutcome.eventWinnerId())
                ? SettlementResult.WON
                : SettlementResult.LOST;

        return new SettlementDecision(
                betId,
                userId,
                eventId,
                eventMarketId,
                eventWinnerId,
                eventOutcome.eventWinnerId(),
                betAmount,
                settlementResult
        );
    }
}
