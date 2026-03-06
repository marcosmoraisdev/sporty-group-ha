package com.sporty.group.eventoutcome.support.builders;

import com.sporty.group.eventoutcome.domain.EventOutcome;

public class EventOutcomeTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

    public static EventOutcomeTestBuilder builder() {
        return new EventOutcomeTestBuilder();
    }

    public EventOutcomeTestBuilder withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventOutcomeTestBuilder withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public EventOutcomeTestBuilder withEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
        return this;
    }

    public EventOutcome build() {
        return EventOutcome.create(eventId, eventName, eventWinnerId);
    }
}
