package com.sporty.groupha.betmatching.infrastructure.persistence;

import com.sporty.groupha.betmatching.infrastructure.entity.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetJpaRepository extends JpaRepository<BetEntity, String> {

    List<BetEntity> findByEventId(String eventId);
}
