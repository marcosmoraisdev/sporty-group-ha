package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import com.sporty.groupha.eventoutcome.support.builders.EventOutcomeRequestTestBuilder;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PublishEventOutcomeService publishEventOutcomeService;

    @MockitoBean
    private EventOutcomeMapper eventOutcomeMapper;

    @Test
    void mapsRequestToDomainAndReturnsAccepted() throws Exception {
        EventOutcomeRequest eventOutcomeRequest = new EventOutcomeRequestTestBuilder().build();
        EventOutcome eventOutcome = EventOutcome.create("event-1", "Match A", "winner-1");
        given(eventOutcomeMapper.toDomain(any(EventOutcomeRequest.class)))
                .willReturn(eventOutcome);

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventOutcomeRequest)))
                .andExpect(status().isAccepted());

        verify(publishEventOutcomeService).publish(eventOutcome);
    }
}
