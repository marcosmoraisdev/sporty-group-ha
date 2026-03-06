package com.sporty.group.betmatching.domain;

import com.sporty.group.betmatching.support.builders.BetTestBuilder;
import com.sporty.group.betmatching.support.builders.EventOutcomeTestBuilder;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BetTest {

    @Test
    void returnsWonWhenBetWinnerMatchesActualWinner() {
        Bet bet = BetTestBuilder.builder().build();
        EventOutcome eventOutcome = EventOutcomeTestBuilder.builder().build();

        SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

        assertThat(settlementDecision.result()).isEqualTo(BetSettlementResult.WON);
    }

    @Test
    void returnsLostWhenBetWinnerDoesNotMatchActualWinner() {
        Bet bet = BetTestBuilder.builder()
                .withEventWinnerId("winner-1")
                .build();
        EventOutcome eventOutcome = EventOutcomeTestBuilder.builder()
                .withEventWinnerId("winner-2")
                .build();

        SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

        assertThat(settlementDecision.result()).isEqualTo(BetSettlementResult.LOST);
    }
}
