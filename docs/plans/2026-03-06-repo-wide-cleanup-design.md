# Repo-Wide Cleanup Design

## Objective
Apply a repo-wide cleanup that standardizes naming, persistence conventions, mapping style, test support, and observability across the mono-repo without changing the external event-driven workflow.

The runtime behavior must remain the same:
- `event-outcome-service` accepts an event outcome over HTTP and publishes it to Kafka.
- `bet-matching-service` consumes the outcome, matches bets, and publishes settlement messages.
- `bet-settlement-service` consumes the settlement message and persists the final settlement result.

## Confirmed Scope
- Update everything in the repository, including code, tests, build files, README, and existing plan/design documents.
- Cover only domain factories and behavior methods that actually contain branching logic.
- Standardize flow logging with `INFO` for normal milestones and `WARN` for invalid or skipped states.
- Replace framework string-empty helper usage in domain objects with equivalent plain Java validation that still rejects `null` and empty strings.

## Approaches Considered

### Recommended: Phased By Concern Across The Repo
Apply each cleanup rule repo-wide before moving to the next concern.

Pros:
- keeps conventions consistent everywhere,
- reduces repeated file churn,
- makes cross-cutting changes like package rename and builder normalization easier to review.

Cons:
- each phase touches multiple modules.

### Alternative: Phased By Module
Complete each service independently from top to bottom.

Pros:
- easier local reasoning inside one module at a time,
- simpler if one service becomes unexpectedly complex.

Cons:
- repeats the same cross-cutting work three times,
- increases the chance of inconsistent conventions between modules.

### Alternative: Big-Bang Rename First
Perform the namespace and package rename first, then apply the remaining refactors.

Pros:
- removes the noisiest rename early.

Cons:
- makes every later diff harder to review,
- causes avoidable re-touching of files.

## Recommended Approach
Use the phased-by-concern approach across the entire repository.

This is the best fit because the requested work is mostly convention cleanup rather than service-specific redesign. Applying one rule at a time keeps the final state consistent for packages, Maven coordinates, builders, UUID generation, entity shape, and docs.

## Design

### 1. Refactor Structure
The cleanup will be organized in four concern-driven phases:

1. Namespace and naming normalization.
   This covers standardizing the codebase on `com.sporty.group`, updating Maven `groupId`, package declarations, imports, stale references in docs, and class naming consistency. `BetSettlementService` remains the canonical service name, and any lingering legacy settlement-service references are removed from tests or docs.

2. Persistence model normalization.
   This covers entity construction rules, UUID generator updates, and schema naming alignment. The event-outcome table name is normalized to `event_outcome`.

3. Boundary mapping and test-support cleanup.
   This covers mapper refinements, test builder standardization, and replacing inline domain construction in tests with test builders.

4. Domain behavior coverage and observability.
   This covers branch-complete domain tests and flow logs that make the event path traceable across services.

### 2. Namespace And Naming Rules
The repository namespace is standardized on `com.sporty.group` everywhere:
- Java package declarations
- imports
- Maven parent and module coordinates
- references in README and plan/design documentation

Module names and artifact names stay unchanged unless a file name or class name explicitly carries the old namespace or outdated service name.

### 3. Persistence Model Rules
All JPA entities will follow the same structure:
- `@Getter`
- `@AllArgsConstructor`
- `@NoArgsConstructor`
- no setters
- `@UuidGenerator` for primary key generation

Entities will gain builders, but only entities. This is the construction path that keeps persistence mappings generated while avoiding mutable setter-based JPA models.

The `event-outcome-service` persistence model will align with the new generalized naming:
- JPA entity table name becomes `event_outcome`
- the current baseline Flyway migration is updated directly instead of adding a follow-up rename migration

This direct migration edit is intentional because the assignment project uses fresh local database bootstraps rather than long-lived production migration history.

### 4. Mapper Rules
MapStruct remains the default mapping approach at boundaries.

The mapper strategy will differ slightly by target type:
- message mappings remain generated MapStruct mappings
- persistence mappings remain generated MapStruct mappings and target entity builders
- mappings into validated domain objects continue to respect domain factories

For validated domain objects such as `EventOutcome` and `BetSettlement`, the design will not allow MapStruct to bypass `create(...)`. Instead, declarative field mapping will be used to remove handwritten field extraction, and a thin wrapper path will keep domain validation authoritative.

This satisfies both goals:
- reduce manual default-method field mapping,
- preserve rich domain model validation.

Specific mapper outcomes:
- `bet-matching-service` `BetSettlementMapper` uses declarative `@Mapping` style for message creation
- `bet-matching-service` `EventOutcomeMapper.toDomain` no longer relies on a field-by-field handwritten default method pattern
- `bet-settlement-service` `BetSettlementMapper.toDomain` no longer performs handwritten field extraction for every source property

### 5. Domain Behavior Rules
Domain validation stays explicit and framework-free.

Where domain logic currently uses a framework string-empty helper, it will be replaced with plain Java checks that preserve behavior:
- reject `null`
- reject `""`

This applies only to domain classes with actual logic. Passive records with no validation or behavior do not gain artificial rules.

### 6. Test Support Rules
All test builders in the repository will use the same entry pattern:
- each builder exposes a static `builder()` method
- tests use the builder static entrypoint instead of direct builder construction

`EventOutcomeTestBuilder` becomes the standard path for domain event outcome test setup in places where tests currently call `EventOutcome.create(...)` inline.

Builders remain scenario-oriented:
- default valid state
- fluent `with...(...)` overrides
- `build()` terminal method

### 7. Domain Test Coverage
Unit test coverage will target branch-bearing domain behavior only.

Targeted classes:
- `event-outcome-service`: `EventOutcome.create(...)`
- `bet-matching-service`: `Bet.settleAgainst(...)`
- `bet-settlement-service`: `BetSettlement.create(...)`

Coverage expectations:
- valid creation path
- each invalid input branch in factory methods
- both settlement outcomes in `Bet.settleAgainst(...)`

The goal is branch completeness for meaningful domain logic, not inflated coverage for passive data holders.

### 8. Logging Design
Logs will be added only where they improve traceability of the processing flow.

Use `INFO` for:
- receiving a valid input
- starting a service step
- persisting a record
- publishing a message
- consuming a message
- completing a settlement step

Use `WARN` for:
- invalid states detected during processing
- skipped handling paths
- no matched bets for an event outcome

No new `DEBUG` logging is planned.

The intended outcome is that one event outcome can be followed through ingestion, matching, publication, and settlement using only the normal application logs.

## Affected Areas
- `common-lib`
- `event-outcome-service`
- `bet-matching-service`
- `bet-settlement-service`
- root Maven configuration
- README
- `docs/plans`

## Risks And Mitigations
- Package and Maven coordinate renames can create broad compile failures.
  Mitigation: apply the namespace rename consistently and verify with module and root test runs.

- Removing entity setters can break generated persistence mappings.
  Mitigation: introduce builders on entities only and let MapStruct target those builders.

- Updating the baseline migration can desynchronize the schema from entity metadata.
  Mitigation: change the Flyway baseline and JPA table mapping together and verify through repository tests.

- Mapper cleanup can accidentally bypass domain validation.
  Mitigation: keep domain factories authoritative for validated domain objects.

- Added logs can become noisy if placed too low in the stack.
  Mitigation: log only at service and listener/publisher flow boundaries.

## Success Criteria
The cleanup is complete when:
- the legacy namespace is fully replaced by `com.sporty.group`
- all requested repo-wide naming and convention updates are applied
- entities use `@UuidGenerator`, constructors, no setters, and entity-only builders
- legacy event-outcome table naming is replaced by `event_outcome`
- test builders use the `builder()` static entry pattern
- domain tests cover all actual branching behavior
- flow logs exist for key normal and skipped processing states
- docs reflect the final naming and conventions
- the repo builds and tests pass
