package com.sporty.groupha.betmatching.support.builders;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;

public class EventOutcomeMessageTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

    public EventOutcomeMessage build() {
        return new EventOutcomeMessage(eventId, eventName, eventWinnerId);
    }
}
