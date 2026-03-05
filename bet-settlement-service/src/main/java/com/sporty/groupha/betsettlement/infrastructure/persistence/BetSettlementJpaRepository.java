package com.sporty.groupha.betsettlement.infrastructure.persistence;

import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetSettlementJpaRepository extends JpaRepository<BetSettlementEntity, String> {
}
