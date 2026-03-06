package com.sporty.group.betmatching.services;

import com.sporty.group.betmatching.domain.Bet;
import com.sporty.group.betmatching.domain.EventOutcome;
import com.sporty.group.betmatching.infrastructure.entity.BetEntity;
import com.sporty.group.betmatching.infrastructure.mappers.BetMapper;
import com.sporty.group.betmatching.infrastructure.mappers.BetSettlementMapper;
import com.sporty.group.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.group.betmatching.support.builders.BetEntityTestBuilder;
import com.sporty.group.betmatching.support.builders.BetSettlementMessageTestBuilder;
import com.sporty.group.betmatching.support.builders.BetTestBuilder;
import com.sporty.group.betmatching.support.builders.EventOutcomeTestBuilder;
import com.sporty.group.commonlib.messaging.EventPublisher;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessEventOutcomeServiceTest {

    @Mock
    private BetJpaRepository betJpaRepository;

    @Mock
    private BetMapper betMapper;

    @Mock
    private BetSettlementMapper betSettlementMapper;

    @Mock
    private EventPublisher<BetSettlementMessage> eventPublisher;

    @InjectMocks
    private ProcessEventOutcomeService processEventOutcomeService;

    @Test
    void publishesOneSettlementMessagePerMatchedBet() {
        EventOutcome eventOutcome = EventOutcomeTestBuilder.builder().build();
        BetEntity betEntity = BetEntityTestBuilder.builder().build();
        Bet bet = BetTestBuilder.builder().build();
        BetSettlementMessage betSettlementMessage = BetSettlementMessageTestBuilder.builder().build();
        given(betJpaRepository.findByEventId("event-1")).willReturn(List.of(betEntity));
        given(betMapper.toDomain(betEntity)).willReturn(bet);
        given(betSettlementMapper.toMessage(any())).willReturn(betSettlementMessage);

        processEventOutcomeService.process(eventOutcome);

        verify(eventPublisher).publish(betSettlementMessage);
    }

    @Test
    void returnsEarlyWhenNoMatchedBetsAreFound() {
        EventOutcome eventOutcome = EventOutcomeTestBuilder.builder().build();
        given(betJpaRepository.findByEventId("event-1")).willReturn(List.of());

        processEventOutcomeService.process(eventOutcome);

        verify(eventPublisher, never()).publish(any());
        verify(betMapper, never()).toDomain(any());
        verify(betSettlementMapper, never()).toMessage(any());
    }
}
