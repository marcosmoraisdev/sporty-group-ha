create table bets (
    bet_id varchar(255) primary key,
    user_id varchar(255) not null,
    event_id varchar(255) not null,
    event_market_id varchar(255) not null,
    event_winner_id varchar(255) not null,
    bet_amount decimal(19,2) not null
);
