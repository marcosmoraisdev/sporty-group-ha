package com.sporty.group.betsettlement.infrastructure.persistence;

import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.group.betsettlement.support.builders.BetSettlementEntityTestBuilder;
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
        BetSettlementEntity betSettlementEntity = BetSettlementEntityTestBuilder.builder().build();

        BetSettlementEntity savedEntity = betSettlementJpaRepository.save(betSettlementEntity);

        assertThat(savedEntity.getId()).isNotNull();
    }
}
