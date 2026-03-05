package com.sporty.groupha.betmatching.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RocketMqBetSettlementPublisher {

    private final RocketMQTemplate rocketMQTemplate;
    private final String topicName;

    public RocketMqBetSettlementPublisher(
            RocketMQTemplate rocketMQTemplate,
            @Value("${app.messaging.bet-settlements-topic}") String topicName
    ) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.topicName = topicName;
    }

    public void publish(BetSettlementMessage betSettlementMessage) {
        rocketMQTemplate.syncSend(topicName, betSettlementMessage);
    }
}
