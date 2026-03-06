package com.sporty.group.betmatching.infrastructure.messaging;

import com.sporty.group.betmatching.support.builders.BetSettlementMessageTestBuilder;
import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
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
        BetSettlementMessage betSettlementMessage = BetSettlementMessageTestBuilder.builder().build();
        EventPublisher<BetSettlementMessage> eventPublisher =
                new RocketMqBetSettlementPublisher(rocketMQTemplate, "bet-settlements");

        eventPublisher.publish(betSettlementMessage);

        verify(rocketMQTemplate).syncSend("bet-settlements", betSettlementMessage);
    }
}
