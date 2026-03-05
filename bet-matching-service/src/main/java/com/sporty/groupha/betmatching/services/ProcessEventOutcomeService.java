package com.sporty.groupha.betmatching.services;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.domain.SettlementDecision;
import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetEntityMapper;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetSettlementMessageMapper;
import com.sporty.groupha.betmatching.infrastructure.messaging.RocketMqBetSettlementPublisher;
import com.sporty.groupha.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessEventOutcomeService {

    private final BetJpaRepository betJpaRepository;
    private final BetEntityMapper betEntityMapper;
    private final BetSettlementMessageMapper betSettlementMessageMapper;
    private final RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher;

    public ProcessEventOutcomeService(
            BetJpaRepository betJpaRepository,
            BetEntityMapper betEntityMapper,
            BetSettlementMessageMapper betSettlementMessageMapper,
            RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher
    ) {
        this.betJpaRepository = betJpaRepository;
        this.betEntityMapper = betEntityMapper;
        this.betSettlementMessageMapper = betSettlementMessageMapper;
        this.rocketMqBetSettlementPublisher = rocketMqBetSettlementPublisher;
    }

    public void process(EventOutcome eventOutcome) {
        String eventId = eventOutcome.eventId();
        List<BetEntity> matchedBetEntities = betJpaRepository.findByEventId(eventId);

        for (BetEntity matchedBetEntity : matchedBetEntities) {
            Bet matchedBet = betEntityMapper.toDomain(matchedBetEntity);
            SettlementDecision settlementDecision = matchedBet.settleAgainst(eventOutcome);
            BetSettlementMessage betSettlementMessage = betSettlementMessageMapper.toMessage(settlementDecision);

            rocketMqBetSettlementPublisher.publish(betSettlementMessage);
        }
    }
}
