package com.sporty.groupha.betmatching.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "bets")
public class BetEntity {

    @Id
    @Column(name = "bet_id", nullable = false)
    private String betId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_market_id", nullable = false)
    private String eventMarketId;

    @Column(name = "event_winner_id", nullable = false)
    private String eventWinnerId;

    @Column(name = "bet_amount", nullable = false)
    private BigDecimal betAmount;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventMarketId() {
        return eventMarketId;
    }

    public void setEventMarketId(String eventMarketId) {
        this.eventMarketId = eventMarketId;
    }

    public String getEventWinnerId() {
        return eventWinnerId;
    }

    public void setEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }
}
