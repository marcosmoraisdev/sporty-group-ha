package com.sporty.groupha.betsettlement.services;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.groupha.betsettlement.infrastructure.mappers.BetSettlementMapper;
import com.sporty.groupha.betsettlement.infrastructure.persistence.BetSettlementJpaRepository;
import com.sporty.groupha.betsettlement.support.builders.BetSettlementEntityTestBuilder;
import com.sporty.groupha.betsettlement.support.builders.BetSettlementTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplyBetSettlementServiceTest {

    @Mock
    private BetSettlementMapper betSettlementMapper;

    @Mock
    private BetSettlementJpaRepository betSettlementJpaRepository;

    @InjectMocks
    private ApplyBetSettlementService applyBetSettlementService;

    @Test
    void savesMappedSettlementEntity() {
        BetSettlement betSettlement = new BetSettlementTestBuilder().build();
        BetSettlementEntity betSettlementEntity = new BetSettlementEntityTestBuilder().build();
        given(betSettlementMapper.toEntity(betSettlement)).willReturn(betSettlementEntity);

        applyBetSettlementService.apply(betSettlement);

        verify(betSettlementJpaRepository).save(betSettlementEntity);
    }
}
