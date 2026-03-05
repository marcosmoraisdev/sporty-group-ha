package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeApiMapper;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeCommand;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublishEventOutcomeService publishEventOutcomeService;

    @MockBean
    private EventOutcomeApiMapper eventOutcomeApiMapper;

    @Test
    void returnsAcceptedForValidPayload() throws Exception {
        PublishEventOutcomeCommand publishEventOutcomeCommand =
                new PublishEventOutcomeCommand("event-1", "Match A", "winner-1");
        given(eventOutcomeApiMapper.toCommand(org.mockito.ArgumentMatchers.any()))
                .willReturn(publishEventOutcomeCommand);

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId":"event-1","eventName":"Match A","eventWinnerId":"winner-1"}
                                """))
                .andExpect(status().isAccepted());
    }
}
