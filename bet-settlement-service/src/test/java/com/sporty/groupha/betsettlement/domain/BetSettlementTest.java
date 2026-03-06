package com.sporty.groupha.betsettlement.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetSettlementTest {

    @Test
    void rejectsEmptyBetId() {
        assertThatThrownBy(() -> BetSettlement.create(
                "",
                "user-1",
                "event-1",
                "market-1",
                "winner-1",
                "winner-1",
                new BigDecimal("10.00"),
                SettlementResult.WON
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("betId");
    }
}
