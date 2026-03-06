package com.sporty.group.betmatching.infrastructure.persistence;

import com.sporty.group.betmatching.infrastructure.entity.BetEntity;
import com.sporty.group.betmatching.support.builders.BetEntityTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BetJpaRepositoryTest {

    @Autowired
    private BetJpaRepository betJpaRepository;

    @Test
    void savesBetWithGeneratedUuidPrimaryKey() {
        BetEntity betEntity = new BetEntityTestBuilder()
                .withBetId("bet-99")
                .build();

        BetEntity savedBetEntity = betJpaRepository.save(betEntity);

        assertThat(savedBetEntity.getId()).isNotNull();
        assertThat(savedBetEntity.getBetId()).isEqualTo("bet-99");
    }

    @Test
    void stillFindsBetsByEventId() {
        assertThat(betJpaRepository.findByEventId("event-1")).isNotEmpty();
    }
}
