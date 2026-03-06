package com.sporty.group.eventoutcome.infrastructure.persistence;

import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import com.sporty.group.eventoutcome.support.builders.EventOutcomeEntityTestBuilder;
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
        EventOutcomeEntity eventOutcomeEntity = EventOutcomeEntityTestBuilder.builder().build();

        EventOutcomeEntity savedEntity = eventOutcomeJpaRepository.save(eventOutcomeEntity);

        assertThat(savedEntity.getId()).isNotNull();
    }
}
