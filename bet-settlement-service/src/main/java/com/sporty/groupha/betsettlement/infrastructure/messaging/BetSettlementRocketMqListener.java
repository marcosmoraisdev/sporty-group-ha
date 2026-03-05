package com.sporty.groupha.betsettlement.infrastructure.messaging;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.infrastructure.mappers.BetSettlementMessageMapper;
import com.sporty.groupha.betsettlement.services.ApplyBetSettlementService;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = "${app.messaging.bet-settlements-topic}",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class BetSettlementRocketMqListener implements RocketMQListener<BetSettlementMessage> {

    private final BetSettlementMessageMapper betSettlementMessageMapper;
    private final ApplyBetSettlementService applyBetSettlementService;

    public BetSettlementRocketMqListener(
            BetSettlementMessageMapper betSettlementMessageMapper,
            ApplyBetSettlementService applyBetSettlementService
    ) {
        this.betSettlementMessageMapper = betSettlementMessageMapper;
        this.applyBetSettlementService = applyBetSettlementService;
    }

    @Override
    public void onMessage(BetSettlementMessage betSettlementMessage) {
        BetSettlement betSettlement = betSettlementMessageMapper.toDomain(betSettlementMessage);

        applyBetSettlementService.apply(betSettlement);
    }
}
