package com.sporty.groupha.eventoutcome.support.builders;

import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;

import java.time.LocalDateTime;

public class EventOutcomeEntityTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";
    private LocalDateTime createdAt = LocalDateTime.of(2026, 3, 6, 10, 0);

    public EventOutcomeEntityTestBuilder withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventOutcomeEntityTestBuilder withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public EventOutcomeEntityTestBuilder withEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
        return this;
    }

    public EventOutcomeEntityTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AcceptedEventOutcomeEntity build() {
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = new AcceptedEventOutcomeEntity();
        acceptedEventOutcomeEntity.setEventId(eventId);
        acceptedEventOutcomeEntity.setEventName(eventName);
        acceptedEventOutcomeEntity.setEventWinnerId(eventWinnerId);
        acceptedEventOutcomeEntity.setCreatedAt(createdAt);
        return acceptedEventOutcomeEntity;
    }
}
