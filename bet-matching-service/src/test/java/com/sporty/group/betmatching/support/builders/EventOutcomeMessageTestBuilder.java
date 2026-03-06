package com.sporty.group.betmatching.support.builders;

import com.sporty.group.commonlib.messaging.event.EventOutcomeMessage;

public class EventOutcomeMessageTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

    public static EventOutcomeMessageTestBuilder builder() {
        return new EventOutcomeMessageTestBuilder();
    }

    public EventOutcomeMessage build() {
        return new EventOutcomeMessage(eventId, eventName, eventWinnerId);
    }
}
