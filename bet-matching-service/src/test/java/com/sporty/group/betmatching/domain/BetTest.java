package com.sporty.group.betmatching.domain;

import com.sporty.group.betmatching.support.builders.BetTestBuilder;
import com.sporty.group.betmatching.support.builders.EventOutcomeTestBuilder;
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
