package com.sporty.group.betmatching.infrastructure.messaging;

import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RocketMqBetSettlementPublisher implements EventPublisher<BetSettlementMessage> {

    private final RocketMQTemplate rocketMQTemplate;
    private final String topicName;

    public RocketMqBetSettlementPublisher(
            RocketMQTemplate rocketMQTemplate,
            @Value("${app.messaging.bet-settlements-topic}") String topicName
    ) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.topicName = topicName;
    }

    @Override
    public void publish(BetSettlementMessage betSettlementMessage) {
        String betId = betSettlementMessage.betId();
        String configuredTopicName = topicName;

        log.info("Publishing settlement message to topic={} betId={}", configuredTopicName, betId);
        rocketMQTemplate.syncSend(topicName, betSettlementMessage);
    }
}
