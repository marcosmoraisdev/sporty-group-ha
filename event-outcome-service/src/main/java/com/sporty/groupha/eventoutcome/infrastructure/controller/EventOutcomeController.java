package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-outcomes")
@RequiredArgsConstructor
public class EventOutcomeController {

    private final PublishEventOutcomeService publishEventOutcomeService;
    private final EventOutcomeMapper eventOutcomeMapper;

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody EventOutcomeRequest eventOutcomeRequest) {
        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeRequest);

        publishEventOutcomeService.publish(eventOutcome);
        return ResponseEntity.accepted().build();
    }
}
