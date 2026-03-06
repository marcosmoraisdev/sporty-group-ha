package com.sporty.group.betsettlement.infrastructure.messaging;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.infrastructure.mappers.BetSettlementMapper;
import com.sporty.group.betsettlement.services.BetSettlementService;
import com.sporty.group.betsettlement.support.builders.BetSettlementMessageTestBuilder;
import com.sporty.group.betsettlement.support.builders.BetSettlementTestBuilder;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BetSettlementRocketMqListenerTest {

    @Mock
    private BetSettlementMapper betSettlementMapper;

    @Mock
    private BetSettlementService betSettlementService;

    @InjectMocks
    private BetSettlementRocketMqListener betSettlementRocketMqListener;

    @Test
    void mapsIncomingMessageAndDelegatesToApplyService() {
        BetSettlementMessage betSettlementMessage = new BetSettlementMessageTestBuilder().build();
        BetSettlement betSettlement = new BetSettlementTestBuilder().build();
        given(betSettlementMapper.toDomain(betSettlementMessage)).willReturn(betSettlement);

        betSettlementRocketMqListener.onMessage(betSettlementMessage);

        verify(betSettlementService).apply(betSettlement);
    }
}
