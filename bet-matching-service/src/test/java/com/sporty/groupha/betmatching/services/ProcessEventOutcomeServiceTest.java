package com.sporty.groupha.betmatching.services;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetEntityMapper;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetSettlementMapper;
import com.sporty.groupha.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.groupha.betmatching.support.builders.BetSettlementMessageTestBuilder;
import com.sporty.groupha.betmatching.support.builders.EventOutcomeTestBuilder;
import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessEventOutcomeServiceTest {

    @Mock
    private BetJpaRepository betJpaRepository;

    @Mock
    private BetEntityMapper betEntityMapper;

    @Mock
    private BetSettlementMapper betSettlementMapper;

    @Mock
    private EventPublisher<BetSettlementMessage> eventPublisher;

    @InjectMocks
    private ProcessEventOutcomeService processEventOutcomeService;

    @Test
    void publishesOneSettlementMessagePerMatchedBet() {
        EventOutcome eventOutcome = new EventOutcomeTestBuilder().build();
        BetEntity betEntity = new BetEntity();
        betEntity.setBetId("bet-1");
        Bet bet = new Bet("bet-1", "user-1", "event-1", "market-1", "winner-1", new BigDecimal("10.00"));
        BetSettlementMessage betSettlementMessage = new BetSettlementMessageTestBuilder().build();
        given(betJpaRepository.findByEventId("event-1")).willReturn(List.of(betEntity));
        given(betEntityMapper.toDomain(betEntity)).willReturn(bet);
        given(betSettlementMapper.toMessage(org.mockito.ArgumentMatchers.any())).willReturn(betSettlementMessage);

        processEventOutcomeService.process(eventOutcome);

        verify(eventPublisher).publish(betSettlementMessage);
    }
}
