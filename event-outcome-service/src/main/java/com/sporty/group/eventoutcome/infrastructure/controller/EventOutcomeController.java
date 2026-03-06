package com.sporty.group.eventoutcome.infrastructure.controller;

import com.sporty.group.eventoutcome.domain.EventOutcome;
import com.sporty.group.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.group.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.group.eventoutcome.services.PublishEventOutcomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/event-outcomes")
@RequiredArgsConstructor
public class EventOutcomeController {

    private final PublishEventOutcomeService publishEventOutcomeService;
    private final EventOutcomeMapper eventOutcomeMapper;

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody EventOutcomeRequest eventOutcomeRequest) {
        log.info("Received event outcome request for eventId={}", eventOutcomeRequest.eventId());

        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeRequest);

        publishEventOutcomeService.publish(eventOutcome);
        return ResponseEntity.accepted().build();
    }
}
