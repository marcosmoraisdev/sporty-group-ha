# Sports Betting Settlement Microservices Design

## Objective
Build an executable backend assignment that simulates sports event outcome handling and bet settlement through a message-driven workflow using Kafka and RocketMQ.

The implementation must satisfy all requirements in `docs/prd/PRD.md` and the home assignment PDF while staying intentionally simple, readable, and interview-appropriate.

## Confirmed Constraints
- Multi-module Maven mono-repo.
- Three Spring Boot microservices:
  - `event-outcome-service`
  - `bet-matching-service`
  - `bet-settlement-service`
- Shared contracts module: `common-lib`.
- Java `17`.
- Spring Boot `3.5.11`.
- MapStruct for all boundary mappings.
- H2 in-memory database in each service.
- Flyway migrations in each service.
- Real Kafka and real RocketMQ in Docker Compose.
- Use Docker images for Kafka and RocketMQ; prefer the latest usable image tags at implementation time and document the chosen tags in the README.
- Unit tests and Spring slice tests only; no integration tests for now.
- Rich domain models with business behavior in the domain layer.
- Controllers only handle HTTP concerns.
- Services orchestrate use cases.
- Repositories, publishers, listeners, schedulers, and persistence entities live in infrastructure and must not contain business logic.
- If scheduled jobs are needed later, they belong in `infrastructure/scheduler` and only trigger services.
- Use enums over strings where applicable.
- Persist enums with `@Enumerated(EnumType.STRING)`.
- Use records when practical for immutable models.
- No builders for object creation.
- No method calls inline as parameters when readability would suffer.

## Recommended Architecture
Use a pragmatic three-service event-driven architecture with strict service boundaries and a light clean architecture / DDD structure.

This is the recommended approach because it:
- matches the assignment exactly,
- demonstrates service separation clearly,
- keeps the codebase executable and understandable,
- shows object-oriented modeling without overengineering.

## Module Structure
- `pom.xml`
  - parent aggregator and dependency management.
- `common-lib`
  - shared immutable message contracts only.
- `event-outcome-service`
  - HTTP ingestion of event outcomes and Kafka publishing.
- `bet-matching-service`
  - Kafka consumption, bet lookup, settlement decision making, RocketMQ publishing.
- `bet-settlement-service`
  - RocketMQ consumption and settlement persistence.

## Service Responsibilities

### `common-lib`
Contains only shared message contracts used between services.

Rules:
- No domain logic.
- No Spring components.
- No persistence code.
- Keep contracts immutable.

### `event-outcome-service`
Responsibilities:
- expose `POST /api/event-outcomes`,
- validate request payload,
- map API DTO to domain input with MapStruct,
- create and validate the `EventOutcome` domain model,
- persist an audit/history row of the accepted outcome,
- publish `EventOutcomeMessage` to Kafka topic `event-outcomes`.

It owns its own H2 database and Flyway migrations.

### `bet-matching-service`
Responsibilities:
- consume `EventOutcomeMessage` from Kafka topic `event-outcomes`,
- load bets by `eventId`,
- evaluate each matched bet against the incoming event outcome,
- create one settlement command per matched bet,
- publish `BetSettlementMessage` to RocketMQ topic `bet-settlements`.

It owns bet persistence, seeded demo data, its own H2 database, and Flyway migrations.

### `bet-settlement-service`
Responsibilities:
- consume `BetSettlementMessage` from RocketMQ topic `bet-settlements`,
- map the message to domain input with MapStruct,
- create and validate the `BetSettlement` domain model,
- persist final settlement records.

It owns its own H2 database and Flyway migrations.

## Package Structure Per Service
Each service should follow this structure:

- `domain`
  - domain records/entities
  - enums
  - invariants
  - business behavior
- `services`
  - application orchestration
  - transaction boundaries
  - use case coordination
- `infrastructure/controller`
  - REST controllers only
- `infrastructure/dto`
  - request/response DTOs only
- `infrastructure/mappers`
  - MapStruct mappers for API, messaging, and persistence boundaries
- `infrastructure/messaging`
  - Kafka and RocketMQ producers/listeners only
- `infrastructure/persistence`
  - Spring Data repositories, JPA entities, database adapters
- `infrastructure/entity`
  - persistence entities if kept separate from repositories
- `infrastructure/scheduler`
  - optional scheduled triggers only, with no business logic

Dependency rule:
- outer layers may depend on inner layers,
- domain must not depend on framework, transport, or persistence concerns.

## Domain Model

### `EventOutcome`
Fields:
- `eventId`
- `eventName`
- `eventWinnerId`

Behavior:
- static factory for creation,
- invariant checks for required values.

### `Bet`
Fields:
- `betId`
- `userId`
- `eventId`
- `eventMarketId`
- `eventWinnerId`
- `betAmount`

Behavior:
- `settleAgainst(EventOutcome)` compares winner IDs,
- returns a settlement decision with `WON` or `LOST`.

### `BetSettlement`
Fields:
- `betId`
- `userId`
- `eventId`
- `eventMarketId`
- `expectedWinnerId`
- `actualWinnerId`
- `betAmount`
- `result`

Behavior:
- static factory validation,
- ownership of settlement state.

### Enums
- `SettlementResult`: `WON`, `LOST`
- add other enums only when they improve clarity immediately.

## Messaging Contracts
Place all shared contracts in `common-lib`.

### `EventOutcomeMessage`
Fields:
- `eventId`
- `eventName`
- `eventWinnerId`

### `BetSettlementMessage`
Fields:
- `betId`
- `userId`
- `eventId`
- `eventMarketId`
- `expectedWinnerId`
- `actualWinnerId`
- `betAmount`
- `result`

## End-to-End Flow
1. Client calls `POST /api/event-outcomes` on `event-outcome-service`.
2. Controller validates the HTTP payload and maps it to a domain command through MapStruct.
3. Service creates an `EventOutcome`, persists an audit row, and publishes `EventOutcomeMessage` to Kafka topic `event-outcomes`.
4. `bet-matching-service` Kafka listener receives the message and maps it to a domain input model.
5. Service loads all bets with matching `eventId`.
6. Each `Bet` evaluates itself against the event outcome and returns a settlement decision.
7. Service maps each decision to `BetSettlementMessage` through MapStruct and publishes it to RocketMQ topic `bet-settlements`.
8. `bet-settlement-service` RocketMQ listener receives the message, maps it to a domain input model, and calls the settlement service.
9. Settlement service creates a `BetSettlement` domain object and persists the final settlement record.

## Persistence Strategy
Each service owns its own isolated H2 in-memory database.

### `event-outcome-service`
Stores accepted event outcomes for auditability and demonstration.

### `bet-matching-service`
Stores bets and seeds sample data using Flyway so the full flow is demonstrable immediately.

### `bet-settlement-service`
Stores final settlement records.

Rules:
- use Flyway migrations from the start,
- keep entities in infrastructure,
- map entities to domain using MapStruct,
- store enums as strings.

## Messaging and Runtime Simplicity
Topics:
- Kafka: `event-outcomes`
- RocketMQ: `bet-settlements`

Deliberate simplifications for v1:
- no outbox,
- no idempotency tables,
- no duplicate message protection,
- no retry scheduler,
- no dead-letter queue strategy,
- no fallback mock RocketMQ mode,
- no cross-service distributed transactions.

Reasoning:
- the assignment values executable clarity over production hardening,
- these concerns can be called out explicitly as follow-up improvements in the README.

## Mapping Strategy
MapStruct is mandatory across all boundaries.

Use it for:
- API DTO -> service command/domain input,
- domain -> message contract,
- message contract -> domain input,
- domain -> persistence entity,
- persistence entity -> domain.

Guidelines:
- use Spring component model,
- fail fast on unmapped target properties,
- avoid manual object construction where a mapper belongs,
- do not use Lombok builders for core object creation.

## Testing Strategy
No integration tests in this phase.

### Domain unit tests
Cover:
- `Bet.settleAgainst(...)` for `WON` and `LOST`,
- domain factory validation and invariants.

### Slice and adapter tests
Cover:
- `@WebMvcTest` for controllers,
- plain unit tests for services with mocked repositories/publishers,
- plain unit tests for Kafka producer/listener adapters,
- plain unit tests for RocketMQ producer/listener adapters,
- `@DataJpaTest` where repository query behavior needs proof,
- mapper tests where mapping logic is non-trivial.

Test objective:
- fast feedback,
- clear proof of business behavior,
- no unnecessary environment bootstrapping.

## Error Handling
- invalid HTTP requests return `400` with clear validation feedback,
- domain validation failures raise explicit exceptions,
- listeners and publishers stay thin and do not implement business rules,
- framework-level failure behavior is accepted for the first version,
- advanced recovery is intentionally deferred.

## Docker Compose Direction
Docker Compose should run the complete local stack:
- Kafka and required dependencies,
- RocketMQ nameserver and broker,
- `event-outcome-service`,
- `bet-matching-service`,
- `bet-settlement-service`.

Guideline:
- use Docker images for Kafka and RocketMQ,
- prefer the latest usable image tags at implementation time,
- if `latest` is unavailable or unsuitable for a required image, use the current latest stable tag and document it.

## README Expectations
The README is part of the deliverable quality signal and must be intentionally strong.

It should include:
- assignment purpose,
- architecture overview,
- module responsibilities,
- design principles used,
- exact application library versions,
- exact Docker image tags chosen for local runtime,
- message flow walkthrough,
- persistence strategy,
- package structure rationale,
- how to run everything with Docker Compose,
- how to call the API,
- sample request payloads,
- expected end-to-end outcome,
- testing strategy,
- trade-offs and intentionally deferred production concerns.

## Implementation Guidance
Keep the implementation simple and explicit.

Preferred style:
- readable names over clever abstractions,
- rich domain behavior where it matters,
- boundary mapping through MapStruct,
- thin transport and persistence adapters,
- clear service orchestration,
- small focused classes.

Avoid:
- speculative abstractions,
- unnecessary interfaces,
- framework leakage into domain,
- business logic in controllers, listeners, repositories, or schedulers.

## Recommended Next Step
Create a detailed implementation plan that breaks the work into small, testable steps and preserves the approved design decisions above.
