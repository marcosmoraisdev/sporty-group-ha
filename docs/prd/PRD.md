# Product Requirements Document (PRD)

## 1. Product Scope
The system processes sports event outcomes and triggers bet settlement through a message-driven workflow.

## 2. Functional Requirements

### FR-1 Event Outcome Ingestion API
The system must provide an API endpoint that accepts a sports event outcome and publishes it to Kafka.

Required event outcome fields:
- `eventId`
- `eventName`
- `eventWinnerId`

Kafka topic:
- `event-outcomes`

### FR-2 Event Outcome Consumption
The system must run a Kafka consumer subscribed to:
- `event-outcomes`

### FR-3 Bet Matching
For each consumed event outcome, the system must identify bets eligible for settlement using `eventId`.

Required bet data model fields:
- `betId`
- `userId`
- `eventId`
- `eventMarketId`
- `eventWinnerId`
- `betAmount`

### FR-4 Settlement Message Production
For each matched bet, the system must produce a settlement message to RocketMQ topic:
- `bet-settlements`

### FR-5 Settlement Consumption and Processing
The system must run a RocketMQ consumer subscribed to:
- `bet-settlements`

The consumer must process settlement messages and settle the corresponding bets.

## 3. Data and Messaging Requirements
- Event outcome messages must contain all fields defined in FR-1.
- Bet settlement decisions must be based on `eventId` matching.
- Settlement messages must be emitted only for bets identified as eligible for settlement.

## 4. Acceptance Criteria
- API publishes event outcomes to `event-outcomes`.
- Kafka consumer receives messages from `event-outcomes`.
- System matches bets using `eventId`.
- RocketMQ producer sends settlement messages to `bet-settlements`.
- RocketMQ consumer receives settlement messages from `bet-settlements` and triggers settlement.
- Bet storage uses an in-memory database.
- Mock producer mode logs payloads when RocketMQ is not integrated.
