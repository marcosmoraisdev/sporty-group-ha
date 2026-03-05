package com.sporty.groupha.eventoutcome.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventOutcomeTest {

    @Test
    void rejectsBlankEventId() {
        assertThatThrownBy(() -> EventOutcome.create(" ", "Match A", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }
}
