package com.sporty.group.betsettlement.infrastructure.persistence;

import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BetSettlementJpaRepository extends JpaRepository<BetSettlementEntity, UUID> {
}
