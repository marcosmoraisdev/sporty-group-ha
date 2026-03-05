package com.sporty.groupha.betsettlement.services;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.groupha.betsettlement.infrastructure.mappers.BetSettlementEntityMapper;
import com.sporty.groupha.betsettlement.infrastructure.persistence.BetSettlementJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyBetSettlementService {

    private final BetSettlementEntityMapper betSettlementEntityMapper;
    private final BetSettlementJpaRepository betSettlementJpaRepository;

    public ApplyBetSettlementService(
            BetSettlementEntityMapper betSettlementEntityMapper,
            BetSettlementJpaRepository betSettlementJpaRepository
    ) {
        this.betSettlementEntityMapper = betSettlementEntityMapper;
        this.betSettlementJpaRepository = betSettlementJpaRepository;
    }

    public void apply(BetSettlement betSettlement) {
        BetSettlementEntity betSettlementEntity = betSettlementEntityMapper.toEntity(betSettlement);

        betSettlementJpaRepository.save(betSettlementEntity);
    }
}
