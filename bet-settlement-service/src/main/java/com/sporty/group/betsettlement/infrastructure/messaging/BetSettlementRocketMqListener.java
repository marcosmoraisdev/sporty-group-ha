package com.sporty.group.betsettlement.infrastructure.messaging;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.infrastructure.mappers.BetSettlementMapper;
import com.sporty.group.betsettlement.services.BetSettlementService;
import com.sporty.group.commonlib.messaging.EventListener;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${app.messaging.bet-settlements-topic}",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class BetSettlementRocketMqListener implements RocketMQListener<BetSettlementMessage>, EventListener<BetSettlementMessage> {

    private final BetSettlementMapper betSettlementMapper;
    private final BetSettlementService betSettlementService;

    @Override
    public void onMessage(BetSettlementMessage betSettlementMessage) {
        BetSettlement betSettlement = betSettlementMapper.toDomain(betSettlementMessage);

        betSettlementService.apply(betSettlement);
    }
}
