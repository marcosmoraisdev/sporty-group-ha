package com.sporty.groupha.betsettlement.services;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.groupha.betsettlement.infrastructure.mappers.BetSettlementMapper;
import com.sporty.groupha.betsettlement.infrastructure.persistence.BetSettlementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyBetSettlementService {

    private final BetSettlementMapper betSettlementMapper;
    private final BetSettlementJpaRepository betSettlementJpaRepository;

    public void apply(BetSettlement betSettlement) {
        BetSettlementEntity betSettlementEntity = betSettlementMapper.toEntity(betSettlement);

        betSettlementJpaRepository.save(betSettlementEntity);
    }
}
