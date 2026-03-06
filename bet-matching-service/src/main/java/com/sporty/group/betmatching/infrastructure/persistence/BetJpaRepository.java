package com.sporty.group.betmatching.infrastructure.persistence;

import com.sporty.group.betmatching.infrastructure.entity.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BetJpaRepository extends JpaRepository<BetEntity, UUID> {

    List<BetEntity> findByEventId(String eventId);
}
