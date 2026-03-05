package com.sporty.groupha.betsettlement.infrastructure.entity;

import com.sporty.groupha.betsettlement.domain.SettlementResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "bet_settlements")
public class BetSettlementEntity {

    @Id
    @Column(name = "bet_id", nullable = false, length = 64)
    private String betId;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;

    @Column(name = "event_market_id", nullable = false, length = 64)
    private String eventMarketId;

    @Column(name = "expected_winner_id", nullable = false, length = 64)
    private String expectedWinnerId;

    @Column(name = "actual_winner_id", nullable = false, length = 64)
    private String actualWinnerId;

    @Column(name = "bet_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal betAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 16)
    private SettlementResult result;

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

    public String getExpectedWinnerId() {
        return expectedWinnerId;
    }

    public void setExpectedWinnerId(String expectedWinnerId) {
        this.expectedWinnerId = expectedWinnerId;
    }

    public String getActualWinnerId() {
        return actualWinnerId;
    }

    public void setActualWinnerId(String actualWinnerId) {
        this.actualWinnerId = actualWinnerId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public SettlementResult getResult() {
        return result;
    }

    public void setResult(SettlementResult result) {
        this.result = result;
    }
}
