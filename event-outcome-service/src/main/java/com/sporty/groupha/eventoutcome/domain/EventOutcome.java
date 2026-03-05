package com.sporty.groupha.eventoutcome.domain;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {

    public static EventOutcome create(String eventId, String eventName, String eventWinnerId) {
        validate("eventId", eventId);
        validate("eventName", eventName);
        validate("eventWinnerId", eventWinnerId);

        return new EventOutcome(eventId.trim(), eventName.trim(), eventWinnerId.trim());
    }

    private static void validate(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
