# Repo-Wide Cleanup Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Apply the approved repo-wide cleanup across code, tests, build files, configs, and docs without changing the external event-driven workflow.

**Architecture:** Execute the refactor in concern-based phases: namespace first, then domain/test support, then persistence/mappers/logging per service, then the docs sweep and full verification. Keep domain factories authoritative, use MapStruct at boundaries, and only add builders to JPA entities.

**Tech Stack:** Java 17, Spring Boot 3.5.11, MapStruct 1.6.3, Lombok, Spring Data JPA, Flyway, JUnit 5, Mockito, AssertJ

---

Preflight:
- The current branch is not clean. Before executing, either create a fresh worktree or confirm that the existing in-progress changes in `bet-settlement-service` are part of this cleanup.
- Treat `bet-settlement-service/src/main/java/com/sporty/group/betsettlement/services/BetSettlementService.java` as the canonical service path. Do not reintroduce the legacy settlement-service name.

### Task 1: Rename Namespace And Maven Coordinates

**Files:**
- Modify: `pom.xml`
- Modify: `common-lib/pom.xml`
- Modify: `event-outcome-service/pom.xml`
- Modify: `bet-matching-service/pom.xml`
- Modify: `bet-settlement-service/pom.xml`
- Modify: `bet-matching-service/src/main/resources/application.yml`
- Move: `common-lib/src/main/java/com/sporty/group` -> `common-lib/src/main/java/com/sporty/group`
- Move: `common-lib/src/test/java/com/sporty/group` -> `common-lib/src/test/java/com/sporty/group`
- Move: `event-outcome-service/src/main/java/com/sporty/group` -> `event-outcome-service/src/main/java/com/sporty/group`
- Move: `event-outcome-service/src/test/java/com/sporty/group` -> `event-outcome-service/src/test/java/com/sporty/group`
- Move: `bet-matching-service/src/main/java/com/sporty/group` -> `bet-matching-service/src/main/java/com/sporty/group`
- Move: `bet-matching-service/src/test/java/com/sporty/group` -> `bet-matching-service/src/test/java/com/sporty/group`
- Move: `bet-settlement-service/src/main/java/com/sporty/group` -> `bet-settlement-service/src/main/java/com/sporty/group`
- Move: `bet-settlement-service/src/test/java/com/sporty/group` -> `bet-settlement-service/src/test/java/com/sporty/group`

**Step 1: Capture the failing/stale inventory**

Run:

```bash
rg -n "com\\.sporty\\.groupha" pom.xml common-lib event-outcome-service bet-matching-service bet-settlement-service README.md docs/plans -S
```

Expected: many matches across POMs, Java packages/imports, and config.

**Step 2: Apply the namespace rename**

Update Maven coordinates and trusted-package config:

```xml
<groupId>com.sporty.group</groupId>
```

```yaml
spring:
  kafka:
    consumer:
      properties:
        spring.json.trusted.packages: com.sporty.group.commonlib.messaging.event
        spring.json.value.default.type: com.sporty.group.commonlib.messaging.event.EventOutcomeMessage
```

Move package directories and update package/import statements to `com.sporty.group...`.

**Step 3: Verify the rename is complete in executable sources**

Run:

```bash
rg -n "com\\.sporty\\.groupha" pom.xml common-lib event-outcome-service bet-matching-service bet-settlement-service -S
```

Expected: no matches.

**Step 4: Compile after the namespace rename**

Run:

```bash
./mvnw -q -DskipTests compile
```

Expected: PASS.

**Step 5: Commit**

```bash
git add pom.xml common-lib event-outcome-service bet-matching-service bet-settlement-service
git commit -m "refactor: rename groupha namespace"
```

### Task 2: Normalize Event Outcome Domain Validation And Test Builders

**Files:**
- Modify: `event-outcome-service/src/main/java/com/sporty/group/eventoutcome/domain/EventOutcome.java`
- Create: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/support/builders/EventOutcomeTestBuilder.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/support/builders/EventOutcomeRequestTestBuilder.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/support/builders/EventOutcomeMessageTestBuilder.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/support/builders/EventOutcomeEntityTestBuilder.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/domain/EventOutcomeTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/infrastructure/controller/EventOutcomeControllerTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/services/PublishEventOutcomeServiceTest.java`

**Step 1: Write the failing test and switch tests to the new builder entrypoint**

Add assertions for all `EventOutcome.create(...)` branches and update test call sites to use `EventOutcomeTestBuilder.builder()` / `EventOutcomeRequestTestBuilder.builder()`.

Example:

```java
@Test
void rejectsEmptyEventWinnerId() {
    assertThatThrownBy(() -> EventOutcome.create("event-1", "Match A", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("eventWinnerId");
}
```

Run:

```bash
./mvnw -q -pl event-outcome-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=EventOutcomeTest,EventOutcomeControllerTest,PublishEventOutcomeServiceTest test
```

Expected: FAIL with missing `builder()` methods or missing `EventOutcomeTestBuilder`.

**Step 2: Implement the plain-Java validation and builder pattern**

Use explicit null/empty checks:

```java
if (eventId == null || eventId.isEmpty()) {
    throw new IllegalArgumentException("eventId must not be blank");
}
```

Add the shared builder entrypoint:

```java
public static EventOutcomeTestBuilder builder() {
    return new EventOutcomeTestBuilder();
}
```

Build domain objects in tests through the builder instead of inline `EventOutcome.create(...)` where the test is not specifically exercising validation.

**Step 3: Run the focused tests**

Run:

```bash
./mvnw -q -pl event-outcome-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=EventOutcomeTest,EventOutcomeControllerTest,PublishEventOutcomeServiceTest test
```

Expected: PASS.

**Step 4: Commit**

```bash
git add event-outcome-service/src/main/java/com/sporty/group/eventoutcome/domain/EventOutcome.java event-outcome-service/src/test/java/com/sporty/group/eventoutcome
git commit -m "test: standardize event outcome builders"
```

### Task 3: Normalize Event Outcome Mapper, Entity, Migration, And Flow Logs

**Files:**
- Modify: `event-outcome-service/src/main/java/com/sporty/group/eventoutcome/infrastructure/mappers/EventOutcomeMapper.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/group/eventoutcome/infrastructure/entity/EventOutcomeEntity.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/group/eventoutcome/services/PublishEventOutcomeService.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/group/eventoutcome/infrastructure/controller/EventOutcomeController.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/group/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisher.java`
- Modify: `event-outcome-service/src/main/resources/db/migration/V1__create_event_outcome.sql`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/infrastructure/persistence/EventOutcomeJpaRepositoryTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/services/PublishEventOutcomeServiceTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/group/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisherTest.java`

**Step 1: Write the failing persistence and service expectations**

Update tests to expect:
- `event_outcome` table naming
- UUID generation via Hibernate `@UuidGenerator`
- builder-based entity construction
- INFO logs for request receipt, persistence, and publish milestones

Run:

```bash
./mvnw -q -pl event-outcome-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=EventOutcomeJpaRepositoryTest,PublishEventOutcomeServiceTest,KafkaEventOutcomePublisherTest test
```

Expected: FAIL because entity metadata, mapper construction path, or table name still reflect the old model.

**Step 2: Implement the mapper/entity/logging changes**

Entity shape:

```java
@Entity
@Table(name = "event_outcome")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventOutcomeEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
}
```

Mapper rule:

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "createdAt", ignore = true)
EventOutcomeEntity toEntity(EventOutcome eventOutcome);
```

Keep domain validation authoritative by routing request-to-domain creation through the domain factory after generated field mapping.

Add INFO logs around:
- incoming controller request
- persistence before publish
- Kafka publish handoff

Align the baseline migration filename and table name on `event_outcome`.

**Step 3: Run the focused tests**

Run:

```bash
./mvnw -q -pl event-outcome-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=EventOutcomeJpaRepositoryTest,PublishEventOutcomeServiceTest,KafkaEventOutcomePublisherTest,EventOutcomeControllerTest test
```

Expected: PASS.

**Step 4: Commit**

```bash
git add event-outcome-service/src/main/java/com/sporty/group/eventoutcome event-outcome-service/src/main/resources/db/migration/V1__create_event_outcome.sql event-outcome-service/src/test/java/com/sporty/group/eventoutcome
git commit -m "refactor: align event outcome persistence"
```

### Task 4: Normalize Bet Matching Domain Tests, Builders, And Declarative Mappers

**Files:**
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/domain/Bet.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/mappers/EventOutcomeMapper.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/mappers/BetSettlementMapper.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/domain/BetTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/support/builders/BetTestBuilder.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/support/builders/EventOutcomeTestBuilder.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/support/builders/EventOutcomeMessageTestBuilder.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/support/builders/BetSettlementMessageTestBuilder.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/support/builders/BetEntityTestBuilder.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/infrastructure/messaging/EventOutcomeKafkaListenerTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/services/ProcessEventOutcomeServiceTest.java`

**Step 1: Write the failing domain and mapper-oriented tests**

Expand `BetTest` to cover both settlement outcomes and convert matching-service tests to `builder()` usage.

Example:

```java
@Test
void returnsLostWhenBetWinnerDoesNotMatchActualWinner() {
    Bet bet = BetTestBuilder.builder().withEventWinnerId("winner-1").build();
    EventOutcome eventOutcome = EventOutcomeTestBuilder.builder().withEventWinnerId("winner-2").build();

    SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

    assertThat(settlementDecision.result()).isEqualTo(SettlementResult.LOST);
}
```

Run:

```bash
./mvnw -q -pl bet-matching-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetTest,EventOutcomeKafkaListenerTest,ProcessEventOutcomeServiceTest test
```

Expected: FAIL with missing builder entrypoints or stale mapper assumptions.

**Step 2: Implement the mapper and builder cleanup**

Use declarative mapping for message creation:

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    @Mapping(target = "result", source = "result")
    BetSettlementMessage toMessage(SettlementDecision settlementDecision);
}
```

Remove field-by-field manual extraction from `EventOutcomeMapper` by introducing a generated field-mapping step and a thin factory-backed wrapper to preserve domain validation where needed.

Add `builder()` to all matching-service test builders.

**Step 3: Run the focused tests**

Run:

```bash
./mvnw -q -pl bet-matching-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetTest,EventOutcomeKafkaListenerTest,ProcessEventOutcomeServiceTest,RocketMqBetSettlementPublisherTest test
```

Expected: PASS.

**Step 4: Commit**

```bash
git add bet-matching-service/src/main/java/com/sporty/group/betmatching/domain/Bet.java bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/mappers bet-matching-service/src/test/java/com/sporty/group/betmatching
git commit -m "refactor: clean up bet matching mappers"
```

### Task 5: Normalize Bet Matching Entity, Repository, And Flow Logs

**Files:**
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/entity/BetEntity.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/mappers/BetMapper.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/services/ProcessEventOutcomeService.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/messaging/EventOutcomeKafkaListener.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/group/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisher.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/infrastructure/persistence/BetJpaRepositoryTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/group/betmatching/services/ProcessEventOutcomeServiceTest.java`

**Step 1: Write the failing repository/service checks**

Update repository and service tests to expect:
- `@UuidGenerator`
- `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- no setter-based entity setup
- INFO logs for consume/process/publish milestones
- WARN logs for no-match cases

Run:

```bash
./mvnw -q -pl bet-matching-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetJpaRepositoryTest,ProcessEventOutcomeServiceTest,EventOutcomeKafkaListenerTest,RocketMqBetSettlementPublisherTest test
```

Expected: FAIL while the entity is still setter-based or logging is incomplete.

**Step 2: Implement the persistence and logging changes**

Entity shape:

```java
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
}
```

Add logs to:
- `EventOutcomeKafkaListener` when a message is consumed
- `ProcessEventOutcomeService` when processing starts, when no bets are found, and after settlement publication
- `RocketMqBetSettlementPublisher` when a settlement command is published

Keep the existing `WARN` for empty matches and add complementary `INFO` milestones around the happy path.

**Step 3: Run the focused tests**

Run:

```bash
./mvnw -q -pl bet-matching-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetJpaRepositoryTest,BetTest,EventOutcomeKafkaListenerTest,ProcessEventOutcomeServiceTest,RocketMqBetSettlementPublisherTest test
```

Expected: PASS.

**Step 4: Commit**

```bash
git add bet-matching-service/src/main/java/com/sporty/group/betmatching bet-matching-service/src/test/java/com/sporty/group/betmatching
git commit -m "refactor: normalize bet matching persistence"
```

### Task 6: Normalize Bet Settlement Domain Validation, Builders, And Mapper

**Files:**
- Modify: `bet-settlement-service/src/main/java/com/sporty/group/betsettlement/domain/BetSettlement.java`
- Modify: `bet-settlement-service/src/main/java/com/sporty/group/betsettlement/infrastructure/mappers/BetSettlementMapper.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/domain/BetSettlementTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/support/builders/BetSettlementTestBuilder.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/support/builders/BetSettlementMessageTestBuilder.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/support/builders/BetSettlementEntityTestBuilder.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/infrastructure/messaging/BetSettlementRocketMqListenerTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/services/BetSettlementServiceTest.java`

**Step 1: Write the failing domain tests**

Expand `BetSettlementTest` to cover:
- valid creation
- null and empty string branches for every required string field
- null `betAmount`
- null `result`

Convert all settlement-service tests to the `builder()` entry pattern.

Run:

```bash
./mvnw -q -pl bet-settlement-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetSettlementTest,BetSettlementRocketMqListenerTest,BetSettlementServiceTest test
```

Expected: FAIL due to missing `builder()` methods and incomplete branch coverage.

**Step 2: Implement plain-Java validation and mapper cleanup**

Use explicit null/empty checks:

```java
if (betId == null || betId.isEmpty()) {
    throw new IllegalArgumentException("betId must not be blank");
}
```

Keep factory-backed domain creation, but remove handwritten field extraction from the mapper by delegating field mapping to MapStruct and only keeping the factory handoff thin.

Add `builder()` to the settlement test builders.

**Step 3: Run the focused tests**

Run:

```bash
./mvnw -q -pl bet-settlement-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetSettlementTest,BetSettlementRocketMqListenerTest,BetSettlementServiceTest test
```

Expected: PASS.

**Step 4: Commit**

```bash
git add bet-settlement-service/src/main/java/com/sporty/group/betsettlement/domain/BetSettlement.java bet-settlement-service/src/main/java/com/sporty/group/betsettlement/infrastructure/mappers/BetSettlementMapper.java bet-settlement-service/src/test/java/com/sporty/group/betsettlement
git commit -m "test: expand bet settlement domain coverage"
```

### Task 7: Normalize Bet Settlement Entity, Service, Listener, And Logs

**Files:**
- Modify: `bet-settlement-service/src/main/java/com/sporty/group/betsettlement/infrastructure/entity/BetSettlementEntity.java`
- Modify: `bet-settlement-service/src/main/java/com/sporty/group/betsettlement/services/BetSettlementService.java`
- Modify: `bet-settlement-service/src/main/java/com/sporty/group/betsettlement/infrastructure/messaging/BetSettlementRocketMqListener.java`
- Modify: `bet-settlement-service/src/main/resources/db/migration/V1__create_bet_settlements.sql`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/infrastructure/persistence/BetSettlementJpaRepositoryTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/infrastructure/messaging/BetSettlementRocketMqListenerTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/group/betsettlement/services/BetSettlementServiceTest.java`

**Step 1: Write the failing persistence/logging checks**

Update tests to expect:
- `@UuidGenerator`
- builder-based entity construction
- no setters
- INFO logs for consume and save milestones

Run:

```bash
./mvnw -q -pl bet-settlement-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetSettlementJpaRepositoryTest,BetSettlementRocketMqListenerTest,BetSettlementServiceTest test
```

Expected: FAIL while the entity still uses setter-style construction or missing logs.

**Step 2: Implement the entity and logging changes**

Entity shape:

```java
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetSettlementEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
}
```

Add INFO logs to:
- `BetSettlementRocketMqListener` when a settlement message is received
- `BetSettlementService` before and after persistence

Keep the current `apply(...)` method name unless a later cleanup explicitly renames that use case.

**Step 3: Run the focused tests**

Run:

```bash
./mvnw -q -pl bet-settlement-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetSettlementJpaRepositoryTest,BetSettlementTest,BetSettlementRocketMqListenerTest,BetSettlementServiceTest test
```

Expected: PASS.

**Step 4: Commit**

```bash
git add bet-settlement-service/src/main/java/com/sporty/group/betsettlement bet-settlement-service/src/main/resources/db/migration/V1__create_bet_settlements.sql bet-settlement-service/src/test/java/com/sporty/group/betsettlement
git commit -m "refactor: normalize bet settlement persistence"
```

### Task 8: Sweep README And Historical Plan Documents

**Files:**
- Modify: `README.md`
- Modify: `docs/plans/2026-03-05-sports-betting-settlement-design.md`
- Modify: `docs/plans/2026-03-05-sports-betting-settlement-implementation-plan.md`
- Modify: `docs/plans/2026-03-06-event-driven-refactor-design.md`
- Modify: `docs/plans/2026-03-06-event-driven-refactor-implementation-plan.md`
- Modify: `docs/plans/2026-03-06-repo-wide-cleanup-design.md`
- Modify: `docs/plans/2026-03-06-repo-wide-cleanup-implementation-plan.md`

**Step 1: Capture the remaining stale documentation references**

Run:

```bash
LEGACY_DOC_PATTERNS="<legacy namespace and deprecated class names>"
rg -n "$LEGACY_DOC_PATTERNS" README.md docs/plans docs/prd -S
```

Expected: matches in README and historical plan files.

**Step 2: Update the docs**

Bring docs in line with the final codebase:
- `com.sporty.group`
- `BetSettlementService`
- `event_outcome`
- `@UuidGenerator`
- plain Java domain validation wording
- `builder()` test-builder usage where examples exist

**Step 3: Verify the docs sweep**

Run:

```bash
LEGACY_DOC_PATTERNS="<legacy namespace and deprecated class names>"
rg -n "$LEGACY_DOC_PATTERNS" README.md docs/plans docs/prd -S
```

Expected: no matches.

**Step 4: Commit**

```bash
git add README.md docs/plans
git commit -m "docs: align plans with repo cleanup"
```

### Task 9: Full Verification And Final Review

**Files:**
- Verify only: repo root and all modified modules

**Step 1: Run module-focused regression tests**

Run:

```bash
./mvnw -q -pl event-outcome-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=EventOutcomeTest,EventOutcomeControllerTest,EventOutcomeJpaRepositoryTest,PublishEventOutcomeServiceTest,KafkaEventOutcomePublisherTest test
./mvnw -q -pl bet-matching-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetTest,BetJpaRepositoryTest,EventOutcomeKafkaListenerTest,ProcessEventOutcomeServiceTest,RocketMqBetSettlementPublisherTest test
./mvnw -q -pl bet-settlement-service -am -Dsurefire.failIfNoSpecifiedTests=false -Dtest=BetSettlementTest,BetSettlementJpaRepositoryTest,BetSettlementRocketMqListenerTest,BetSettlementServiceTest test
```

Expected: PASS for all three commands.

**Step 2: Run full repo verification**

Run:

```bash
./mvnw -q test
./mvnw -q -DskipTests compile
```

Expected: PASS.

**Step 3: Run final stale-pattern checks**

Run:

```bash
LEGACY_CODE_PATTERNS="<legacy namespace, deprecated builders, and removed mapping patterns>"
rg -n "$LEGACY_CODE_PATTERNS" common-lib event-outcome-service bet-matching-service bet-settlement-service README.md docs/plans -S
```

Expected: no matches.

**Step 4: Review the final diff**

Run:

```bash
git status --short
git diff --stat
```

Expected: only intended cleanup changes remain.

**Step 5: Commit**

```bash
git add .
git commit -m "refactor: apply repo-wide cleanup conventions"
```
