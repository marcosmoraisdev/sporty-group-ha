package com.sporty.group.betmatching.domain;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
