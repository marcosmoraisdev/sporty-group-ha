package com.sporty.group.eventoutcome.infrastructure.mappers;

import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import com.sporty.group.eventoutcome.support.builders.EventOutcomeTestBuilder;
import com.sporty.group.eventoutcome.support.builders.EventOutcomeRequestTestBuilder;
import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;
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
    void mapsDomainToEntityWithIgnoredGeneratedFieldsLeftUnset() {
        EventOutcome eventOutcome = EventOutcomeTestBuilder.builder().build();

        EventOutcomeEntity eventOutcomeEntity = eventOutcomeMapper.toEntity(eventOutcome);

        assertThat(eventOutcomeEntity.getId()).isNull();
        assertThat(eventOutcomeEntity.getCreatedAt()).isNull();
        assertThat(eventOutcomeEntity.getEventId()).isEqualTo("event-1");
        assertThat(eventOutcomeEntity.getEventName()).isEqualTo("Match A");
        assertThat(eventOutcomeEntity.getEventWinnerId()).isEqualTo("winner-1");
    }

    @Test
    void mapsDomainToMessage() {
        EventOutcome eventOutcome = EventOutcomeTestBuilder.builder().build();

        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMapper.toMessage(eventOutcome);

        assertThat(eventOutcomeMessage.eventId()).isEqualTo("event-1");
        assertThat(eventOutcomeMessage.eventName()).isEqualTo("Match A");
        assertThat(eventOutcomeMessage.eventWinnerId()).isEqualTo("winner-1");
    }
}
