package com.sporty.group.commonlib.messaging.settlement;

import java.math.BigDecimal;

public record BetSettlementMessage(
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
