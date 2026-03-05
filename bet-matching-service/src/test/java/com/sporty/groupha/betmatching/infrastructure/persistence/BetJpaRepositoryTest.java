package com.sporty.groupha.betmatching.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BetJpaRepositoryTest {

    @Autowired
    private BetJpaRepository betJpaRepository;

    @Test
    void findsBetsByEventId() {
        assertThat(betJpaRepository.findByEventId("event-1")).isNotEmpty();
    }
}
