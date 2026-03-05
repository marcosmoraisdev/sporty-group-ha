package com.sporty.groupha.betsettlement.infrastructure.messaging;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.domain.SettlementResult;
import com.sporty.groupha.betsettlement.infrastructure.mappers.BetSettlementMessageMapper;
import com.sporty.groupha.betsettlement.services.ApplyBetSettlementService;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BetSettlementRocketMqListenerTest {

    @Mock
    private BetSettlementMessageMapper betSettlementMessageMapper;

    @Mock
    private ApplyBetSettlementService applyBetSettlementService;

    @InjectMocks
    private BetSettlementRocketMqListener betSettlementRocketMqListener;

    @Test
    void delegatesConsumedMessageToService() {
        BetSettlementMessage betSettlementMessage = new BetSettlementMessage(
                "bet-1",
                "user-1",
                "event-1",
                "market-1",
                "winner-1",
                "winner-1",
                new BigDecimal("10.00"),
                BetSettlementResult.WON
        );
        BetSettlement betSettlement = BetSettlement.create(
                "bet-1",
                "user-1",
                "event-1",
                "market-1",
                "winner-1",
                "winner-1",
                new BigDecimal("10.00"),
                SettlementResult.WON
        );
        given(betSettlementMessageMapper.toDomain(betSettlementMessage)).willReturn(betSettlement);

        betSettlementRocketMqListener.onMessage(betSettlementMessage);

        verify(applyBetSettlementService).apply(betSettlement);
    }
}
