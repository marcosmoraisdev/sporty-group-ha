package com.sporty.groupha.eventoutcome.infrastructure.persistence;

import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AcceptedEventOutcomeJpaRepositoryTest {

    @Autowired
    private AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;

    @Test
    void savesEntity() {
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = new AcceptedEventOutcomeEntity();
        acceptedEventOutcomeEntity.setEventId("event-1");
        acceptedEventOutcomeEntity.setEventName("Match A");
        acceptedEventOutcomeEntity.setEventWinnerId("winner-1");

        AcceptedEventOutcomeEntity savedEntity = acceptedEventOutcomeJpaRepository.save(acceptedEventOutcomeEntity);

        assertThat(savedEntity.getId()).isNotNull();
    }
}
