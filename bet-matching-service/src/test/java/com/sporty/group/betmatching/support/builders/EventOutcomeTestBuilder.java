package com.sporty.group.betmatching.support.builders;

import com.sporty.group.betmatching.domain.EventOutcome;

public class EventOutcomeTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

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
        return new EventOutcome(eventId, eventName, eventWinnerId);
    }
}
