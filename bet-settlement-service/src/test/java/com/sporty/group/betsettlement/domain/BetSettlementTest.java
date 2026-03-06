package com.sporty.group.betsettlement.domain;

import com.sporty.group.betsettlement.support.builders.BetSettlementTestBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetSettlementTest {

    @Test
    void createsSettlementWithValidValues() {
        BetSettlement betSettlement = BetSettlementTestBuilder.builder().build();

        assertThat(betSettlement.betId()).isEqualTo("bet-1");
        assertThat(betSettlement.userId()).isEqualTo("user-1");
        assertThat(betSettlement.eventId()).isEqualTo("event-1");
        assertThat(betSettlement.eventMarketId()).isEqualTo("market-1");
        assertThat(betSettlement.expectedWinnerId()).isEqualTo("winner-1");
        assertThat(betSettlement.actualWinnerId()).isEqualTo("winner-1");
        assertThat(betSettlement.betAmount()).isEqualByComparingTo("10.00");
        assertThat(betSettlement.result()).isEqualTo(SettlementResult.WON);
    }

    @Test
    void rejectsNullBetId() {
        assertRejectsBlank(builder -> builder.withBetId(null), "betId");
    }

    @Test
    void rejectsEmptyBetId() {
        assertRejectsBlank(builder -> builder.withBetId(""), "betId");
    }

    @Test
    void rejectsNullUserId() {
        assertRejectsBlank(builder -> builder.withUserId(null), "userId");
    }

    @Test
    void rejectsEmptyUserId() {
        assertRejectsBlank(builder -> builder.withUserId(""), "userId");
    }

    @Test
    void rejectsNullEventId() {
        assertRejectsBlank(builder -> builder.withEventId(null), "eventId");
    }

    @Test
    void rejectsEmptyEventId() {
        assertRejectsBlank(builder -> builder.withEventId(""), "eventId");
    }

    @Test
    void rejectsNullEventMarketId() {
        assertRejectsBlank(builder -> builder.withEventMarketId(null), "eventMarketId");
    }

    @Test
    void rejectsEmptyEventMarketId() {
        assertRejectsBlank(builder -> builder.withEventMarketId(""), "eventMarketId");
    }

    @Test
    void rejectsNullExpectedWinnerId() {
        assertRejectsBlank(builder -> builder.withExpectedWinnerId(null), "expectedWinnerId");
    }

    @Test
    void rejectsEmptyExpectedWinnerId() {
        assertRejectsBlank(builder -> builder.withExpectedWinnerId(""), "expectedWinnerId");
    }

    @Test
    void rejectsNullActualWinnerId() {
        assertRejectsBlank(builder -> builder.withActualWinnerId(null), "actualWinnerId");
    }

    @Test
    void rejectsEmptyActualWinnerId() {
        assertRejectsBlank(builder -> builder.withActualWinnerId(""), "actualWinnerId");
    }

    @Test
    void rejectsNullBetAmount() {
        assertThatThrownBy(() -> BetSettlementTestBuilder.builder()
                .withBetAmount(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("betAmount");
    }

    @Test
    void rejectsNullResult() {
        assertThatThrownBy(() -> BetSettlementTestBuilder.builder()
                .withResult(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("result");
    }

    private void assertRejectsBlank(
            UnaryOperator<BetSettlementTestBuilder> customizer,
            String fieldName
    ) {
        assertThatThrownBy(() -> customizer.apply(BetSettlementTestBuilder.builder()).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(fieldName);
    }
}
