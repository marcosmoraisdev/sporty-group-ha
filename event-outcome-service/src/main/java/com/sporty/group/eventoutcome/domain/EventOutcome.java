package com.sporty.group.eventoutcome.domain;

import org.springframework.util.StringUtils;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {

    public static EventOutcome create(String eventId, String eventName, String eventWinnerId) {
        if (StringUtils.isEmpty(eventId)) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (StringUtils.isEmpty(eventName)) {
            throw new IllegalArgumentException("eventName must not be blank");
        }
        if (StringUtils.isEmpty(eventWinnerId)) {
            throw new IllegalArgumentException("eventWinnerId must not be blank");
        }

        return new EventOutcome(eventId, eventName, eventWinnerId);
    }
}
