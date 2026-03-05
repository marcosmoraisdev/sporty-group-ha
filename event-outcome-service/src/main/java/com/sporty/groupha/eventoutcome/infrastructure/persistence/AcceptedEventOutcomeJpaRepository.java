package com.sporty.groupha.eventoutcome.infrastructure.persistence;

import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedEventOutcomeJpaRepository extends JpaRepository<AcceptedEventOutcomeEntity, Long> {
}
