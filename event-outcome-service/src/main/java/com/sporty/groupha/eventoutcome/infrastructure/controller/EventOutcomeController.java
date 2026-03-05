package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeApiMapper;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeCommand;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-outcomes")
public class EventOutcomeController {

    private final PublishEventOutcomeService publishEventOutcomeService;
    private final EventOutcomeApiMapper eventOutcomeApiMapper;

    public EventOutcomeController(
            PublishEventOutcomeService publishEventOutcomeService,
            EventOutcomeApiMapper eventOutcomeApiMapper
    ) {
        this.publishEventOutcomeService = publishEventOutcomeService;
        this.eventOutcomeApiMapper = eventOutcomeApiMapper;
    }

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody EventOutcomeRequest eventOutcomeRequest) {
        PublishEventOutcomeCommand publishEventOutcomeCommand = eventOutcomeApiMapper.toCommand(eventOutcomeRequest);

        publishEventOutcomeService.publish(publishEventOutcomeCommand);
        return ResponseEntity.accepted().build();
    }
}
