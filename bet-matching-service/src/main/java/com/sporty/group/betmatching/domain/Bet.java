package com.sporty.group.betmatching.domain;

import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;

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
        BetSettlementResult settlementResult = eventWinnerId.equals(eventOutcome.eventWinnerId())
                ? BetSettlementResult.WON
                : BetSettlementResult.LOST;

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
