package com.sporty.groupha.betmatching.domain;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
