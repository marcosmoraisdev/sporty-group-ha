package com.sporty.group.eventoutcome.infrastructure.mappers;

import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.group.eventoutcome.support.builders.EventOutcomeRequestTestBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventOutcomeMapperTest {

    private final EventOutcomeMapper eventOutcomeMapper = Mappers.getMapper(EventOutcomeMapper.class);

    @Test
    void mapsValidRequestToDomain() {
        EventOutcomeRequest eventOutcomeRequest = EventOutcomeRequestTestBuilder.builder().build();

        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeRequest);

        assertThat(eventOutcome.eventId()).isEqualTo("event-1");
        assertThat(eventOutcome.eventName()).isEqualTo("Match A");
        assertThat(eventOutcome.eventWinnerId()).isEqualTo("winner-1");
    }

    @Test
    void rejectsInvalidRequestValuesThroughMapper() {
        EventOutcomeRequest eventOutcomeRequest = EventOutcomeRequestTestBuilder.builder()
                .withEventId(null)
                .build();

        assertThatThrownBy(() -> eventOutcomeMapper.toDomain(eventOutcomeRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }
}
