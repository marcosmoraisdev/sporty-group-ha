package com.sporty.group.betsettlement.infrastructure.entity;

import com.sporty.group.betsettlement.domain.SettlementResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bet_settlements")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetSettlementEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
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
}
