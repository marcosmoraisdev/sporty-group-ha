package com.sporty.groupha.betsettlement.infrastructure.persistence;

import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.groupha.betsettlement.support.builders.BetSettlementEntityTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BetSettlementJpaRepositoryTest {

    @Autowired
    private BetSettlementJpaRepository betSettlementJpaRepository;

    @Test
    void savesBetSettlementWithGeneratedUuidId() {
        BetSettlementEntity betSettlementEntity = new BetSettlementEntityTestBuilder().build();

        BetSettlementEntity savedEntity = betSettlementJpaRepository.save(betSettlementEntity);

        assertThat(savedEntity.getId()).isNotNull();
    }
}
