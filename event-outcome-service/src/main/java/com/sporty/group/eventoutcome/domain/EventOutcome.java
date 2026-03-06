package com.sporty.group.eventoutcome.domain;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {

    public static EventOutcome create(String eventId, String eventName, String eventWinnerId) {
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (eventName == null || eventName.isEmpty()) {
            throw new IllegalArgumentException("eventName must not be blank");
        }
        if (eventWinnerId == null || eventWinnerId.isEmpty()) {
            throw new IllegalArgumentException("eventWinnerId must not be blank");
        }

        return new EventOutcome(eventId, eventName, eventWinnerId);
    }
}
