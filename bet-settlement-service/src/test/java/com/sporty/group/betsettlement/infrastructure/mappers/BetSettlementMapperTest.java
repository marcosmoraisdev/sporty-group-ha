package com.sporty.group.betsettlement.infrastructure.mappers;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.domain.SettlementResult;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;
import com.sporty.group.betsettlement.support.builders.BetSettlementMessageTestBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetSettlementMapperTest {

    private final BetSettlementMapper betSettlementMapper = Mappers.getMapper(BetSettlementMapper.class);

    @Test
    void mapsValidMessageToDomain() {
        BetSettlementMessage betSettlementMessage = BetSettlementMessageTestBuilder.builder().build();

        BetSettlement betSettlement = betSettlementMapper.toDomain(betSettlementMessage);

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
    void rejectsInvalidMessageValuesThroughMapper() {
        BetSettlementMessage betSettlementMessage = new BetSettlementMessage(
                null,
                "user-1",
                "event-1",
                "market-1",
                "winner-1",
                "winner-1",
                new BigDecimal("10.00"),
                BetSettlementResult.WON
        );

        assertThatThrownBy(() -> betSettlementMapper.toDomain(betSettlementMessage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("betId");
    }
}
