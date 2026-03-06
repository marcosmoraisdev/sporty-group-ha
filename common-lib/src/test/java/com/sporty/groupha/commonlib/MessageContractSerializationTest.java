package com.sporty.groupha.commonlib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.groupha.commonlib.messaging.EventListener;
import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageContractSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void exposesGenericEventMessagingContracts() {
        EventPublisher<EventOutcomeMessage> publisher = event -> {
        };
        EventListener<EventOutcomeMessage> listener = event -> {
        };

        assertThat(publisher).isNotNull();
        assertThat(listener).isNotNull();
    }

    @Test
    void serializesEventOutcomeMessage() throws Exception {
        EventOutcomeMessage message = new EventOutcomeMessage("event-1", "Arsenal vs Benfica", "team-7");

        String json = objectMapper.writeValueAsString(message);

        assertThat(json)
                .contains("event-1")
                .contains("Arsenal vs Benfica")
                .contains("team-7");
    }
}
