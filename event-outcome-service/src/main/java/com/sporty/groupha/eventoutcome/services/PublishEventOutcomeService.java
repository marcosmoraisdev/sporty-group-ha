package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import org.springframework.stereotype.Service;

@Service
public class PublishEventOutcomeService {

    public void publish(PublishEventOutcomeCommand publishEventOutcomeCommand) {
        EventOutcome.create(
                publishEventOutcomeCommand.eventId(),
                publishEventOutcomeCommand.eventName(),
                publishEventOutcomeCommand.eventWinnerId()
        );
    }
}
