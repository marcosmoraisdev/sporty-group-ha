package com.sporty.groupha.betsettlement.infrastructure.entity;

import com.sporty.groupha.betsettlement.domain.SettlementResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bet_settlements")
@Getter
public class BetSettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bet_id", nullable = false, unique = true, length = 64)
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventMarketId(String eventMarketId) {
        this.eventMarketId = eventMarketId;
    }

    public void setExpectedWinnerId(String expectedWinnerId) {
        this.expectedWinnerId = expectedWinnerId;
    }

    public void setActualWinnerId(String actualWinnerId) {
        this.actualWinnerId = actualWinnerId;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public void setResult(SettlementResult result) {
        this.result = result;
    }
}
