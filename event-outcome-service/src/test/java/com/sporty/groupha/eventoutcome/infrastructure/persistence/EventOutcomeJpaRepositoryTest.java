package com.sporty.groupha.eventoutcome.infrastructure.persistence;

import com.sporty.groupha.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import com.sporty.groupha.eventoutcome.support.builders.EventOutcomeEntityTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventOutcomeJpaRepositoryTest {

    @Autowired
    private EventOutcomeJpaRepository eventOutcomeJpaRepository;

    @Test
    void savesEventOutcomeWithGeneratedUuidId() {
        EventOutcomeEntity eventOutcomeEntity = new EventOutcomeEntityTestBuilder().build();

        EventOutcomeEntity savedEntity = eventOutcomeJpaRepository.save(eventOutcomeEntity);

        assertThat(savedEntity.getId()).isNotNull();
    }
}
