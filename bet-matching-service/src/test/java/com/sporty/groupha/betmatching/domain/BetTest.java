package com.sporty.groupha.betmatching.domain;

import com.sporty.groupha.betmatching.support.builders.BetTestBuilder;
import com.sporty.groupha.betmatching.support.builders.EventOutcomeTestBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BetTest {

    @Test
    void returnsWonWhenBetWinnerMatchesActualWinner() {
        Bet bet = new BetTestBuilder().build();
        EventOutcome eventOutcome = new EventOutcomeTestBuilder().build();

        SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

        assertThat(settlementDecision.result()).isEqualTo(SettlementResult.WON);
    }
}
