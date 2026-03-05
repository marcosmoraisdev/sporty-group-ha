package com.sporty.groupha.eventoutcome.services;

public record PublishEventOutcomeCommand(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
