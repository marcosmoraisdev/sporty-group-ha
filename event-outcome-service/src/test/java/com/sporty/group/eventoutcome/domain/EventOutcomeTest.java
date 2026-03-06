package com.sporty.group.eventoutcome.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventOutcomeTest {

    @Test
    void createsEventOutcomeWithoutTrimmingValues() {
        EventOutcome eventOutcome = EventOutcome.create(" event-1 ", " Match A ", " winner-1 ");

        assertThat(eventOutcome.eventId()).isEqualTo(" event-1 ");
        assertThat(eventOutcome.eventName()).isEqualTo(" Match A ");
        assertThat(eventOutcome.eventWinnerId()).isEqualTo(" winner-1 ");
    }

    @Test
    void rejectsNullEventId() {
        assertThatThrownBy(() -> EventOutcome.create(null, "Match A", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }

    @Test
    void rejectsEmptyEventId() {
        assertThatThrownBy(() -> EventOutcome.create("", "Match A", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }

    @Test
    void rejectsNullEventName() {
        assertThatThrownBy(() -> EventOutcome.create("event-1", null, "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventName");
    }

    @Test
    void rejectsEmptyEventName() {
        assertThatThrownBy(() -> EventOutcome.create("event-1", "", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventName");
    }

    @Test
    void rejectsNullEventWinnerId() {
        assertThatThrownBy(() -> EventOutcome.create("event-1", "Match A", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventWinnerId");
    }

    @Test
    void rejectsEmptyEventWinnerId() {
        assertThatThrownBy(() -> EventOutcome.create("event-1", "Match A", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventWinnerId");
    }
}
