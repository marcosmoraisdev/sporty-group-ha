package com.sporty.group.betsettlement.domain;

import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;

import java.math.BigDecimal;

public record BetSettlement(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        BetSettlementResult result
) {

    public static BetSettlement create(
            String betId,
            String userId,
            String eventId,
            String eventMarketId,
            String expectedWinnerId,
            String actualWinnerId,
            BigDecimal betAmount,
            BetSettlementResult result
    ) {
        if (betId == null || betId.isEmpty()) {
            throw new IllegalArgumentException("betId must not be blank");
        }
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (eventMarketId == null || eventMarketId.isEmpty()) {
            throw new IllegalArgumentException("eventMarketId must not be blank");
        }
        if (expectedWinnerId == null || expectedWinnerId.isEmpty()) {
            throw new IllegalArgumentException("expectedWinnerId must not be blank");
        }
        if (actualWinnerId == null || actualWinnerId.isEmpty()) {
            throw new IllegalArgumentException("actualWinnerId must not be blank");
        }
        if (betAmount == null) {
            throw new IllegalArgumentException("betAmount must not be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        return new BetSettlement(
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
