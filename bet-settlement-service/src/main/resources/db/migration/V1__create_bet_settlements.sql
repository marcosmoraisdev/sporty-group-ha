CREATE TABLE bet_settlements (
    id UUID PRIMARY KEY,
    bet_id VARCHAR(64) NOT NULL UNIQUE,
    user_id VARCHAR(64) NOT NULL,
    event_id VARCHAR(64) NOT NULL,
    event_market_id VARCHAR(64) NOT NULL,
    expected_winner_id VARCHAR(64) NOT NULL,
    actual_winner_id VARCHAR(64) NOT NULL,
    bet_amount DECIMAL(19, 2) NOT NULL,
    result VARCHAR(16) NOT NULL
);
