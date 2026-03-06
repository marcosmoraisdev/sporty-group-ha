package com.sporty.groupha.betmatching.infrastructure.messaging;

import com.sporty.groupha.betmatching.support.builders.BetSettlementMessageTestBuilder;
import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RocketMqBetSettlementPublisherTest {

    @Mock
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void sendsSettlementMessageToConfiguredTopic() {
        BetSettlementMessage betSettlementMessage = new BetSettlementMessageTestBuilder().build();
        EventPublisher<BetSettlementMessage> eventPublisher =
                new RocketMqBetSettlementPublisher(rocketMQTemplate, "bet-settlements");

        eventPublisher.publish(betSettlementMessage);

        verify(rocketMQTemplate).syncSend("bet-settlements", betSettlementMessage);
    }
}
