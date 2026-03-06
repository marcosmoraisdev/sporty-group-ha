package com.sporty.group.commonlib.messaging.event;

public record EventOutcomeMessage(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
