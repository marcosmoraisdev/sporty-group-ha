package com.sporty.groupha.betmatching.services;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetEntityMapper;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetSettlementMessageMapper;
import com.sporty.groupha.betmatching.infrastructure.messaging.RocketMqBetSettlementPublisher;
import com.sporty.groupha.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementResult;
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
    private BetSettlementMessageMapper betSettlementMessageMapper;

    @Mock
    private RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher;

    @InjectMocks
    private ProcessEventOutcomeService processEventOutcomeService;

    @Test
    void publishesOneSettlementMessagePerMatchedBet() {
        EventOutcome eventOutcome = new EventOutcome("event-1", "Match A", "winner-1");
        BetEntity betEntity = new BetEntity();
        betEntity.setBetId("bet-1");
        Bet bet = new Bet("bet-1", "user-1", "event-1", "market-1", "winner-1", new BigDecimal("10.00"));
        BetSettlementMessage betSettlementMessage = new BetSettlementMessage(
                "bet-1",
                "user-1",
                "event-1",
                "market-1",
                "winner-1",
                "winner-1",
                new BigDecimal("10.00"),
                BetSettlementResult.WON
        );
        given(betJpaRepository.findByEventId("event-1")).willReturn(List.of(betEntity));
        given(betEntityMapper.toDomain(betEntity)).willReturn(bet);
        given(betSettlementMessageMapper.toMessage(org.mockito.ArgumentMatchers.any())).willReturn(betSettlementMessage);

        processEventOutcomeService.process(eventOutcome);

        verify(rocketMqBetSettlementPublisher).publish(betSettlementMessage);
    }
}
