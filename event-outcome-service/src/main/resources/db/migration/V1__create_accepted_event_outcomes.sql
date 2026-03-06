create table event_outcome (
    id uuid primary key,
    event_id varchar(255) not null,
    event_name varchar(255) not null,
    event_winner_id varchar(255) not null,
    created_at timestamp not null
);
