package com.sporty.groupha.betmatching.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RocketMqBetSettlementPublisherTest {

    @Mock
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void sendsSettlementMessageToConfiguredTopic() {
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
        RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher =
                new RocketMqBetSettlementPublisher(rocketMQTemplate, "bet-settlements");

        rocketMqBetSettlementPublisher.publish(betSettlementMessage);

        verify(rocketMQTemplate).syncSend("bet-settlements", betSettlementMessage);
    }
}
