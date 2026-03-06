package com.sporty.groupha.betsettlement.domain;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public record BetSettlement(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        SettlementResult result
) {

    public static BetSettlement create(
            String betId,
            String userId,
            String eventId,
            String eventMarketId,
            String expectedWinnerId,
            String actualWinnerId,
            BigDecimal betAmount,
            SettlementResult result
    ) {
        if (StringUtils.isEmpty(betId)) {
            throw new IllegalArgumentException("betId must not be blank");
        }
        if (StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (StringUtils.isEmpty(eventId)) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (StringUtils.isEmpty(eventMarketId)) {
            throw new IllegalArgumentException("eventMarketId must not be blank");
        }
        if (StringUtils.isEmpty(expectedWinnerId)) {
            throw new IllegalArgumentException("expectedWinnerId must not be blank");
        }
        if (StringUtils.isEmpty(actualWinnerId)) {
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
