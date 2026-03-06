package com.sporty.group.betsettlement.services;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.group.betsettlement.infrastructure.mappers.BetSettlementMapper;
import com.sporty.group.betsettlement.infrastructure.persistence.BetSettlementJpaRepository;
import com.sporty.group.betsettlement.support.builders.BetSettlementEntityTestBuilder;
import com.sporty.group.betsettlement.support.builders.BetSettlementTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BetSettlementServiceTest {

    @Mock
    private BetSettlementMapper betSettlementMapper;

    @Mock
    private BetSettlementJpaRepository betSettlementJpaRepository;

    @InjectMocks
    private BetSettlementService betSettlementService;

    @Test
    void savesMappedSettlementEntity() {
        BetSettlement betSettlement = new BetSettlementTestBuilder().build();
        BetSettlementEntity betSettlementEntity = new BetSettlementEntityTestBuilder().build();
        given(betSettlementMapper.toEntity(betSettlement)).willReturn(betSettlementEntity);

        betSettlementService.apply(betSettlement);

        verify(betSettlementJpaRepository).save(betSettlementEntity);
    }
}
