package com.sporty.group.betmatching.services;

import com.sporty.group.betmatching.domain.EventOutcome;
import com.sporty.group.betmatching.infrastructure.entity.BetEntity;
import com.sporty.group.betmatching.infrastructure.mappers.BetMapper;
import com.sporty.group.betmatching.infrastructure.mappers.BetSettlementMapper;
import com.sporty.group.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessEventOutcomeService {

    private final BetJpaRepository betJpaRepository;
    private final BetMapper betMapper;
    private final BetSettlementMapper betSettlementMapper;
    private final EventPublisher<BetSettlementMessage> eventPublisher;

    public void process(EventOutcome eventOutcome) {
        List<BetEntity> matchedBetEntities = betJpaRepository.findByEventId(eventOutcome.eventId());

        if(matchedBetEntities.isEmpty()) {
            log.warn("No bets found for event outcome with eventId: {}", eventOutcome.eventId());
            return;
        }

        matchedBetEntities.stream()
                .map(betMapper::toDomain)
                .map(matchedBet -> matchedBet.settleAgainst(eventOutcome))
                .map(betSettlementMapper::toMessage)
                .forEach(eventPublisher::publish);
    }
}
