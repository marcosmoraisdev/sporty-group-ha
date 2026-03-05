package com.sporty.groupha.betmatching.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BetTest {

    @Test
    void returnsWonWhenBetWinnerMatchesActualWinner() {
        Bet bet = new Bet("bet-1", "user-1", "event-1", "market-1", "winner-1", new BigDecimal("10.00"));
        EventOutcome eventOutcome = new EventOutcome("event-1", "Match A", "winner-1");

        SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

        assertThat(settlementDecision.result()).isEqualTo(SettlementResult.WON);
    }
}
