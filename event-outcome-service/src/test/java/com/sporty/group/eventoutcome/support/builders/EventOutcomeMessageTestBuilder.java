package com.sporty.group.eventoutcome.support.builders;

import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;

public class EventOutcomeMessageTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

    public EventOutcomeMessageTestBuilder withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventOutcomeMessageTestBuilder withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public EventOutcomeMessageTestBuilder withEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
        return this;
    }

    public EventOutcomeMessage build() {
        return new EventOutcomeMessage(eventId, eventName, eventWinnerId);
    }
}
