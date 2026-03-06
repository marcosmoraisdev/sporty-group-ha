# Event-Driven Refactor Design

## Objective
Refactor the existing sports betting mono-repo to tighten architectural boundaries, simplify object mapping, modernize test support, and standardize persistence and style conventions without changing the external workflow of the three services.

The refactor must preserve the current end-to-end behavior:
- `event-outcome-service` accepts event outcomes over HTTP and publishes them.
- `bet-matching-service` consumes outcomes, matches bets, and publishes settlements.
- `bet-settlement-service` consumes settlements and persists them.

## In-Scope Changes
- Remove record-level generic `validate(...)` helpers and keep explicit validation logic inside domain factory methods.
- Replace handwritten constructors in Spring-managed classes with Lombok `@RequiredArgsConstructor`.
- Replace deprecated `@MockBean` usage with the current Spring test bean override annotation.
- Refactor `EventOutcomeController` to map `EventOutcomeRequest` directly to domain `EventOutcome` and remove `PublishEventOutcomeCommand`.
- Delete all `ApplicationContextTest.java` tests.
- Introduce shared message ports named `EventPublisher` and `EventListener`.
- Rename `AcceptedEventOutcomeEntity` to `EventOutcomeEntity`.
- Add Lombok `@Getter` to JPA entities.
- Convert JPA primary keys to UUID.
- Consolidate boundary mapping to one mapper per domain in each module.
- Remove inline imports and keep imports at the top of the class.
- Expand `EventOutcomeTest` to cover all validation branches.
- Replace ad hoc object construction in tests with test builders.
- Convert `BetEntityMapper` to generated MapStruct mapping.
- Prefer streams where they improve readability over simple `for` loops.

## Confirmed Constraints
- Only JPA primary keys move to UUID.
- Business identifiers such as `betId`, `eventId`, `userId`, `eventMarketId`, and winner IDs remain strings.
- `BetEntity` and `BetSettlementEntity` gain surrogate UUID primary keys instead of changing their existing business identifiers.
- The refactor should preserve service boundaries and avoid new cross-service coupling.
- MapStruct remains the default mapping approach at module boundaries.
- Domain models must remain free of Spring, messaging, and persistence concerns.

## Recommended Approach
Use a targeted architectural cleanup that preserves the current runtime shape while removing infrastructure leakage from services and reducing duplication in mapping and testing.

This approach is preferred because it:
- addresses every requested item,
- keeps behavior stable,
- limits schema changes to the minimum needed for UUID primary keys,
- and avoids a larger rewrite that would make the refactor harder to review.

## Architecture Changes

### Shared Messaging Ports
Add generic contracts to `common-lib`:
- `EventPublisher<T>`
- `EventListener<T>`

These interfaces provide the shared vocabulary for messaging boundaries. They do not know about Kafka or RocketMQ.

Infrastructure adapters remain transport-specific:
- Kafka publisher/listener classes stay in `infrastructure/messaging`.
- RocketMQ publisher/listener classes stay in `infrastructure/messaging`.
- The existing adapter classes implement the shared contracts.

Application services depend on the shared abstractions:
- `PublishEventOutcomeService` depends on `EventPublisher<EventOutcomeMessage>`.
- `ProcessEventOutcomeService` depends on `EventPublisher<BetSettlementMessage>`.

Listener classes stay in infrastructure because they own broker annotations and message ingress. They may implement `EventListener<T>` for naming consistency, but business orchestration continues to flow into application services.

### Controller and Service Boundary Cleanup
`EventOutcomeController` maps `EventOutcomeRequest` directly to domain `EventOutcome` through a domain-focused mapper and calls the service with the domain object.

`PublishEventOutcomeCommand` is removed because it duplicates the domain shape without adding behavior or protecting a separate application contract.

## Domain Changes

### Event Outcome Validation
`event-outcome-service` keeps validation in `EventOutcome.create(...)`, but removes the private generic `validate(...)` method.

Each required field is checked directly in the factory method using explicit `if` statements. The factory no longer trims values before constructing the record.

### Domain Style Rules
The refactor will standardize the current code toward the repo conventions:
- extract values into named variables before invocation,
- avoid inline method calls in parameters,
- keep imports at the top of the class,
- prefer enums over strings where already applicable,
- use streams only where they are clearer than imperative loops.

## Persistence Changes

### Entity Renaming
`AcceptedEventOutcomeEntity` is renamed to `EventOutcomeEntity`.

Associated code is renamed with it:
- `AcceptedEventOutcomeJpaRepository` -> `EventOutcomeJpaRepository`
- `AcceptedEventOutcomeEntityMapper` -> folded into the consolidated `EventOutcomeMapper`
- all tests and references update to the new naming

The table name can stay as-is if that reduces migration churn, but the Java-side model and repository names should reflect the generalized concept.

### UUID Primary Keys
All JPA entities use UUID primary keys after the refactor.

Specific changes:
- `event-outcome-service`
  - replace the current numeric identity `id` with UUID primary key on `EventOutcomeEntity`
- `bet-matching-service`
  - add a new UUID `id` primary key to `BetEntity`
  - keep `betId` as the existing business identifier and repository query field
- `bet-settlement-service`
  - add a new UUID `id` primary key to `BetSettlementEntity`
  - keep `betId` as the settlement business identifier

Repository signatures and repository tests update accordingly.

Flyway migrations are updated or extended to support the new UUID primary key model in each service.

### Lombok on Entities
JPA entities receive Lombok `@Getter` to reduce handwritten accessors.

Setters remain only where the entity lifecycle still requires them. The refactor should not introduce Lombok patterns that conflict with JPA construction requirements.

## Mapping Consolidation
Mapping is reorganized around domains instead of transport or persistence type names.

Target mapper layout:

### `event-outcome-service`
- `EventOutcomeMapper`
  - `EventOutcomeRequest` -> `EventOutcome`
  - `EventOutcome` -> `EventOutcomeEntity`
  - `EventOutcome` -> `EventOutcomeMessage`

### `bet-matching-service`
- `EventOutcomeMapper`
  - `EventOutcomeMessage` -> `EventOutcome`
- `BetMapper`
  - `BetEntity` -> `Bet`
- `BetSettlementMapper`
  - `SettlementDecision` -> `BetSettlementMessage`

### `bet-settlement-service`
- `BetSettlementMapper`
  - `BetSettlementMessage` -> `BetSettlement`
  - `BetSettlement` -> `BetSettlementEntity`

`BetEntityMapper` becomes a generated MapStruct mapper instead of a manual default-method mapper.

## Test Strategy

### Test Infrastructure
- Remove all `ApplicationContextTest.java` files.
- Replace `@MockBean` with `@MockitoBean` in Spring test slices.

Spring slice tests should remain narrow and explicit.

### Test Builders
Introduce module-local test builders to replace repetitive manual construction in tests.

Expected usage:
- build domain objects,
- build message contracts,
- build entities,
- override only the fields relevant to the scenario under test.

### Coverage Improvements
`EventOutcomeTest` is expanded to cover:
- valid creation,
- empty `eventId`,
- empty `eventName`,
- empty `eventWinnerId`,
- null variants if currently supported by the API boundary assumptions.

Service and messaging tests update to the new mapper and abstraction names, and should verify:
- persistence happens before publishing in `PublishEventOutcomeService`,
- listeners delegate correctly after mapping,
- `ProcessEventOutcomeService` publishes one settlement per matched bet,
- stream-based refactors preserve behavior.

## Rollout Plan
The implementation should proceed in narrow, reviewable stages:

1. Add Lombok support and shared messaging ports in `common-lib`.
2. Refactor `event-outcome-service` domain, controller, service, mapper, persistence, and tests.
3. Refactor `bet-matching-service` messaging dependencies, mapper layout, entity primary key, and tests.
4. Refactor `bet-settlement-service` listener abstraction, mapper layout, entity primary key, and tests.
5. Remove obsolete tests and run module and root verification.

## Risks and Mitigations
- Schema changes for UUID primary keys can break repository tests.
  - Mitigation: update Flyway migrations together with JPA mappings and validate via repository tests.
- Mapper consolidation can accidentally change Spring bean wiring.
  - Mitigation: update tests at each service boundary before changing wiring.
- Replacing `@MockBean` incorrectly can break web slice tests.
  - Mitigation: use Spring Framework 6.2 `@MockitoBean`, which is the current bean override mechanism used in tests.
- Stream conversions can reduce readability if overused.
  - Mitigation: only replace loops where the stream is obviously clearer and keeps side effects explicit.

## Success Criteria
The refactor is complete when:
- all requested refactors are applied,
- services depend on `EventPublisher` instead of Kafka or RocketMQ publisher classes directly,
- listeners and publishers are named by role rather than broker at the service boundary,
- event outcome persistence and mapping names are normalized,
- JPA entities use UUID primary keys,
- tests use builders instead of repetitive raw constructors,
- all module tests and the root build pass.
