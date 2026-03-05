CREATE TABLE bet_settlements (
    bet_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    event_id VARCHAR(64) NOT NULL,
    event_market_id VARCHAR(64) NOT NULL,
    expected_winner_id VARCHAR(64) NOT NULL,
    actual_winner_id VARCHAR(64) NOT NULL,
    bet_amount DECIMAL(19, 2) NOT NULL,
    result VARCHAR(16) NOT NULL
);
