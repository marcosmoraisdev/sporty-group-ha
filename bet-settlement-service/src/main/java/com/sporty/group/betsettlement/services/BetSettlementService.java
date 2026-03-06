package com.sporty.group.betsettlement.services;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.group.betsettlement.infrastructure.mappers.BetSettlementMapper;
import com.sporty.group.betsettlement.infrastructure.persistence.BetSettlementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BetSettlementService {

    private final BetSettlementMapper betSettlementMapper;
    private final BetSettlementJpaRepository betSettlementJpaRepository;

    public void apply(BetSettlement betSettlement) {
        BetSettlementEntity betSettlementEntity = betSettlementMapper.toEntity(betSettlement);

        betSettlementJpaRepository.save(betSettlementEntity);
    }
}
