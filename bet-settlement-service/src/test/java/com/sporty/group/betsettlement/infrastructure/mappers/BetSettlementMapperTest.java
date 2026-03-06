package com.sporty.group.betsettlement.infrastructure.mappers;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.support.builders.BetSettlementMessageTestBuilder;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(betSettlement.result()).isEqualTo(BetSettlementResult.WON);
    }
}
