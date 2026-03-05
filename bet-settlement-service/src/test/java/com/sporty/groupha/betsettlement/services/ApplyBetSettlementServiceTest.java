package com.sporty.groupha.betsettlement.services;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.domain.SettlementResult;
import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.groupha.betsettlement.infrastructure.mappers.BetSettlementEntityMapper;
import com.sporty.groupha.betsettlement.infrastructure.persistence.BetSettlementJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplyBetSettlementServiceTest {

    @Mock
    private BetSettlementEntityMapper betSettlementEntityMapper;

    @Mock
    private BetSettlementJpaRepository betSettlementJpaRepository;

    @InjectMocks
    private ApplyBetSettlementService applyBetSettlementService;

    @Test
    void savesMappedSettlementEntity() {
        BetSettlement betSettlement = BetSettlement.create(
                "bet-1",
                "user-1",
                "event-1",
                "market-1",
                "winner-1",
                "winner-1",
                new BigDecimal("10.00"),
                SettlementResult.WON
        );
        BetSettlementEntity betSettlementEntity = new BetSettlementEntity();
        given(betSettlementEntityMapper.toEntity(betSettlement)).willReturn(betSettlementEntity);

        applyBetSettlementService.apply(betSettlement);

        verify(betSettlementJpaRepository).save(betSettlementEntity);
    }
}
