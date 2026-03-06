package com.sporty.groupha.betmatching.services;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetMapper;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetSettlementMapper;
import com.sporty.groupha.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessEventOutcomeService {

    private final BetJpaRepository betJpaRepository;
    private final BetMapper betMapper;
    private final BetSettlementMapper betSettlementMapper;
    private final EventPublisher<BetSettlementMessage> eventPublisher;

    public void process(EventOutcome eventOutcome) {
        String eventId = eventOutcome.eventId();
        List<BetEntity> matchedBetEntities = betJpaRepository.findByEventId(eventId);

        matchedBetEntities.stream()
                .map(betMapper::toDomain)
                .map(matchedBet -> matchedBet.settleAgainst(eventOutcome))
                .map(betSettlementMapper::toMessage)
                .forEach(eventPublisher::publish);
    }
}
