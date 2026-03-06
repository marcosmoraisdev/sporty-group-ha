package com.sporty.group.eventoutcome.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accepted_event_outcomes")
@Getter
public class EventOutcomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_winner_id", nullable = false)
    private String eventWinnerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
