package com.sporty.groupha.commonlib.messaging.event;

public record EventOutcomeMessage(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
