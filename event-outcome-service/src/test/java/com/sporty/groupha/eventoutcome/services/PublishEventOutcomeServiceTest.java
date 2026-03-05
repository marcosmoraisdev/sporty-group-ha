package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.AcceptedEventOutcomeEntityMapper;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PublishEventOutcomeServiceTest {

    @Mock
    private AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;

    @Mock
    private AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper;

    @InjectMocks
    private PublishEventOutcomeService publishEventOutcomeService;

    @Test
    void savesAcceptedOutcome() {
        PublishEventOutcomeCommand publishEventOutcomeCommand =
                new PublishEventOutcomeCommand("event-1", "Match A", "winner-1");
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = new AcceptedEventOutcomeEntity();
        given(acceptedEventOutcomeEntityMapper.toEntity(org.mockito.ArgumentMatchers.any()))
                .willReturn(acceptedEventOutcomeEntity);

        publishEventOutcomeService.publish(publishEventOutcomeCommand);

        verify(acceptedEventOutcomeJpaRepository).save(acceptedEventOutcomeEntity);
    }
}
