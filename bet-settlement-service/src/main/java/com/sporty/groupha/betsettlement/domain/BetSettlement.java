package com.sporty.groupha.betsettlement.domain;

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
        validate("betId", betId);
        validate("userId", userId);
        validate("eventId", eventId);
        validate("eventMarketId", eventMarketId);
        validate("expectedWinnerId", expectedWinnerId);
        validate("actualWinnerId", actualWinnerId);
        validateAmount(betAmount);
        validateResult(result);

        String sanitizedBetId = betId.trim();
        String sanitizedUserId = userId.trim();
        String sanitizedEventId = eventId.trim();
        String sanitizedEventMarketId = eventMarketId.trim();
        String sanitizedExpectedWinnerId = expectedWinnerId.trim();
        String sanitizedActualWinnerId = actualWinnerId.trim();

        return new BetSettlement(
                sanitizedBetId,
                sanitizedUserId,
                sanitizedEventId,
                sanitizedEventMarketId,
                sanitizedExpectedWinnerId,
                sanitizedActualWinnerId,
                betAmount,
                result
        );
    }

    private static void validate(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    private static void validateAmount(BigDecimal betAmount) {
        if (betAmount == null) {
            throw new IllegalArgumentException("betAmount must not be null");
        }
    }

    private static void validateResult(SettlementResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }
    }
}
