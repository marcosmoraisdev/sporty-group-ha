package com.sporty.group.eventoutcome.infrastructure.persistence;

import com.sporty.group.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventOutcomeJpaRepository extends JpaRepository<EventOutcomeEntity, UUID> {
}
