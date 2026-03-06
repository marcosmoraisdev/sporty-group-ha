insert into bets (id, bet_id, user_id, event_id, event_market_id, event_winner_id, bet_amount) values
    (random_uuid(), 'bet-1', 'user-1', 'event-1', 'market-1', 'winner-1', 10.00),
    (random_uuid(), 'bet-2', 'user-2', 'event-1', 'market-1', 'winner-2', 20.00),
    (random_uuid(), 'bet-3', 'user-3', 'event-2', 'market-2', 'winner-3', 15.00);
