package com.sporty.group.eventoutcome.support.builders;

import com.sporty.group.eventoutcome.infrastructure.dto.EventOutcomeRequest;

public class EventOutcomeRequestTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

    public static EventOutcomeRequestTestBuilder builder() {
        return new EventOutcomeRequestTestBuilder();
    }

    public EventOutcomeRequestTestBuilder withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventOutcomeRequestTestBuilder withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public EventOutcomeRequestTestBuilder withEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
        return this;
    }

    public EventOutcomeRequest build() {
        return new EventOutcomeRequest(eventId, eventName, eventWinnerId);
    }
}
