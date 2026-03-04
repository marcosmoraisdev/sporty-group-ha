# Sports Betting Settlement Microservices Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build three executable microservices that process sports outcomes from API to Kafka, match bets, publish settlements to RocketMQ, and settle bets with read APIs.

**Architecture:** Use a microservices event-driven architecture with strict service boundaries: `sports-outcome-service`, `bet-matching-service`, and `bet-settlement-service`. Each service follows Clean Architecture and DDD-style layers (`domain`, `application`, `infrastructure`, `interfaces`) with MapStruct for DTO-domain-entity mapping and Flyway-managed H2 persistence.

**Tech Stack:** Java 17, Spring Boot 3.5.11, Spring Kafka, RocketMQ Spring Boot starter, H2, Flyway, MapStruct, Maven, Docker, Docker Compose, JUnit 5, MockMvc, AssertJ, Mockito.

---

**Skill References:** `@test-driven-development`, `@verification-before-completion`, `@requesting-code-review`

### Task 1: Bootstrap Multi-Module Workspace and Service Skeletons

**Files:**
- Create: `pom.xml`
- Create: `contracts/pom.xml`
- Create: `services/sports-outcome-service/pom.xml`
- Create: `services/bet-matching-service/pom.xml`
- Create: `services/bet-settlement-service/pom.xml`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/SportsOutcomeApplication.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/BetMatchingApplication.java`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/BetSettlementApplication.java`
- Test: `services/sports-outcome-service/src/test/java/com/sporty/outcome/ApplicationContextTest.java`
- Test: `services/bet-matching-service/src/test/java/com/sporty/matching/ApplicationContextTest.java`
- Test: `services/bet-settlement-service/src/test/java/com/sporty/settlement/ApplicationContextTest.java`

**Step 1: Write the failing test**

```java
package com.sporty.outcome;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationContextTest {
    @Test
    void contextLoads() {
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/sports-outcome-service -Dtest=ApplicationContextTest test -q`
Expected: FAIL with missing module/app class or missing parent pom.

**Step 3: Write minimal implementation**

```java
package com.sporty.outcome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SportsOutcomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SportsOutcomeApplication.class, args);
    }
}
```

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.sporty</groupId>
  <artifactId>sporty-group-ha</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>pom</packaging>
  <modules>
    <module>contracts</module>
    <module>services/sports-outcome-service</module>
    <module>services/bet-matching-service</module>
    <module>services/bet-settlement-service</module>
  </modules>
</project>
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/sports-outcome-service,services/bet-matching-service,services/bet-settlement-service test -Dtest=ApplicationContextTest -q`
Expected: PASS for all 3 services.

**Step 5: Commit**

```bash
git add pom.xml contracts/pom.xml services/**/pom.xml services/**/src/main services/**/src/test
git commit -m "chore: bootstrap multi-module microservices workspace"
```

### Task 2: Add Shared Message Contracts Module

**Files:**
- Create: `contracts/src/main/java/com/sporty/contracts/event/EventOutcomeMessage.java`
- Create: `contracts/src/main/java/com/sporty/contracts/settlement/BetSettlementMessage.java`
- Create: `contracts/src/test/java/com/sporty/contracts/ContractSerializationTest.java`
- Modify: `contracts/pom.xml`

**Step 1: Write the failing test**

```java
package com.sporty.contracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.contracts.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ContractSerializationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesEventOutcomeMessage() throws Exception {
        EventOutcomeMessage msg = new EventOutcomeMessage("evt-1", "Match A", "team-7", "v1", "trace-1", "2026-03-04T10:00:00Z");
        String json = mapper.writeValueAsString(msg);
        assertThat(json).contains("evt-1").contains("schemaVersion");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl contracts -Dtest=ContractSerializationTest test -q`
Expected: FAIL with missing `EventOutcomeMessage`.

**Step 3: Write minimal implementation**

```java
package com.sporty.contracts.event;

public record EventOutcomeMessage(
        String eventId,
        String eventName,
        String eventWinnerId,
        String schemaVersion,
        String traceId,
        String occurredAt
) {}
```

```java
package com.sporty.contracts.settlement;

public record BetSettlementMessage(
        String settlementId,
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        String result,
        String betAmount,
        String traceId,
        String occurredAt
) {}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl contracts -Dtest=ContractSerializationTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add contracts
 git commit -m "feat: add shared event and settlement message contracts"
```

### Task 3: Implement `sports-outcome-service` POST API with Validation and Mapping

**Files:**
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/interfaces/rest/EventOutcomeController.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/interfaces/rest/dto/EventOutcomeRequest.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/application/port/in/PublishEventOutcomeUseCase.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/application/command/PublishEventOutcomeCommand.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/infrastructure/mapping/EventOutcomeApiMapper.java`
- Test: `services/sports-outcome-service/src/test/java/com/sporty/outcome/interfaces/rest/EventOutcomeControllerTest.java`

**Step 1: Write the failing test**

```java
@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private PublishEventOutcomeUseCase useCase;

    @Test
    void returns202ForValidPayload() throws Exception {
        mvc.perform(post("/api/v1/event-outcomes")
                .contentType("application/json")
                .content("""
                    {"eventId":"evt-1","eventName":"Match A","eventWinnerId":"team-1"}
                """))
            .andExpect(status().isAccepted());
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/sports-outcome-service -Dtest=EventOutcomeControllerTest test -q`
Expected: FAIL with missing controller/DTO/use case.

**Step 3: Write minimal implementation**

```java
@RestController
@RequestMapping("/api/v1/event-outcomes")
@RequiredArgsConstructor
class EventOutcomeController {
    private final PublishEventOutcomeUseCase useCase;
    private final EventOutcomeApiMapper mapper;

    @PostMapping
    ResponseEntity<Void> publish(@Valid @RequestBody EventOutcomeRequest request) {
        useCase.publish(mapper.toCommand(request));
        return ResponseEntity.accepted().build();
    }
}
```

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeApiMapper {
    PublishEventOutcomeCommand toCommand(EventOutcomeRequest request);
}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/sports-outcome-service -Dtest=EventOutcomeControllerTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/sports-outcome-service
git commit -m "feat: add event outcome ingestion API with mapstruct mapping"
```

### Task 4: Implement Kafka Publishing and Outcome Read API in `sports-outcome-service`

**Files:**
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/application/service/PublishEventOutcomeService.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/application/port/out/EventOutcomePublisherPort.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/infrastructure/messaging/KafkaEventOutcomePublisher.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/infrastructure/persistence/EventOutcomeEntity.java`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/infrastructure/persistence/EventOutcomeJpaRepository.java`
- Create: `services/sports-outcome-service/src/main/resources/db/migration/V1__create_event_outcomes.sql`
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/interfaces/rest/EventOutcomeQueryController.java`
- Test: `services/sports-outcome-service/src/test/java/com/sporty/outcome/infrastructure/messaging/KafkaEventOutcomePublisherTest.java`
- Test: `services/sports-outcome-service/src/test/java/com/sporty/outcome/interfaces/rest/EventOutcomeQueryControllerTest.java`

**Step 1: Write the failing test**

```java
@ExtendWith(MockitoExtension.class)
class KafkaEventOutcomePublisherTest {
    @Mock KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;
    @InjectMocks KafkaEventOutcomePublisher publisher;

    @Test
    void sendsMessageToEventOutcomesTopic() {
        PublishEventOutcomeCommand cmd = new PublishEventOutcomeCommand(
            "evt-1", "Match A", "team-1", "trace-1", "2026-03-04T10:00:00Z"
        );
        publisher.publish(cmd);
        verify(kafkaTemplate).send(eq("event-outcomes"), eq("evt-1"), any(EventOutcomeMessage.class));
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/sports-outcome-service -Dtest=KafkaEventOutcomePublisherTest test -q`
Expected: FAIL with missing Kafka producer adapter.

**Step 3: Write minimal implementation**

```java
@Service
@RequiredArgsConstructor
class PublishEventOutcomeService implements PublishEventOutcomeUseCase {
    private final EventOutcomePublisherPort publisher;

    @Override
    public void publish(PublishEventOutcomeCommand command) {
        publisher.publish(command);
    }
}
```

```java
@Component
@RequiredArgsConstructor
class KafkaEventOutcomePublisher implements EventOutcomePublisherPort {
    private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;

    @Override
    public void publish(PublishEventOutcomeCommand command) {
        var msg = new EventOutcomeMessage(command.eventId(), command.eventName(), command.eventWinnerId(), "v1", command.traceId(), command.occurredAt());
        kafkaTemplate.send("event-outcomes", command.eventId(), msg);
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/sports-outcome-service -Dtest=KafkaEventOutcomePublisherTest,EventOutcomeQueryControllerTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/sports-outcome-service/src/main services/sports-outcome-service/src/test
git commit -m "feat: publish event outcomes to kafka and expose outcome read API"
```

### Task 5: Implement Bet Persistence, Seed Data, and Read API in `bet-matching-service`

**Files:**
- Create: `services/bet-matching-service/src/main/resources/db/migration/V1__create_bets_table.sql`
- Create: `services/bet-matching-service/src/main/resources/db/migration/V2__seed_bets.sql`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/persistence/BetEntity.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/persistence/BetJpaRepository.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/interfaces/rest/BetQueryController.java`
- Test: `services/bet-matching-service/src/test/java/com/sporty/matching/infrastructure/persistence/BetJpaRepositoryTest.java`

**Step 1: Write the failing test**

```java
@DataJpaTest
class BetJpaRepositoryTest {
    @Autowired BetJpaRepository repository;

    @Test
    void findsSeededBetsByEventId() {
        var bets = repository.findByEventId("evt-1");
        assertThat(bets).isNotEmpty();
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/bet-matching-service -Dtest=BetJpaRepositoryTest test -q`
Expected: FAIL with missing repository/entity/migrations.

**Step 3: Write minimal implementation**

```java
@Entity
@Table(name = "bets")
class BetEntity {
    @Id String betId;
    String userId;
    String eventId;
    String eventMarketId;
    String eventWinnerId;
    BigDecimal betAmount;
    String status;
}
```

```sql
INSERT INTO bets (bet_id, user_id, event_id, event_market_id, event_winner_id, bet_amount, status)
VALUES ('bet-1','user-1','evt-1','mkt-1','team-1',10.00,'OPEN');
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/bet-matching-service -Dtest=BetJpaRepositoryTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/bet-matching-service
git commit -m "feat: add bet persistence with flyway seed data and query endpoints"
```

### Task 6: Consume Kafka and Publish Settlement Commands in `bet-matching-service`

**Files:**
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/messaging/KafkaEventOutcomeListener.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/application/service/GradeBetsService.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/application/port/out/SettlementPublisherPort.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/messaging/RocketMqSettlementPublisher.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/mapping/SettlementMessageMapper.java`
- Test: `services/bet-matching-service/src/test/java/com/sporty/matching/infrastructure/messaging/KafkaEventOutcomeListenerTest.java`
- Test: `services/bet-matching-service/src/test/java/com/sporty/matching/infrastructure/messaging/RocketMqSettlementPublisherTest.java`

**Step 1: Write the failing test**

```java
@ExtendWith(MockitoExtension.class)
class KafkaEventOutcomeListenerTest {
    @Mock GradeBetsService gradeBetsService;
    @InjectMocks KafkaEventOutcomeListener listener;

    @Test
    void delegatesOutcomeMessageToGradingService() {
        EventOutcomeMessage msg = new EventOutcomeMessage("evt-1", "Match A", "team-1", "v1", "trace-1", "2026-03-04T10:00:00Z");
        listener.onMessage(msg);
        verify(gradeBetsService).handleOutcome(msg);
    }
}

@ExtendWith(MockitoExtension.class)
class RocketMqSettlementPublisherTest {
    @Mock RocketMQTemplate rocketMQTemplate;
    @InjectMocks RocketMqSettlementPublisher publisher;

    @Test
    void publishesSettlementMessageToBetSettlementsTopic() {
        BetSettlementMessage msg = new BetSettlementMessage("set-1", "bet-1", "user-1", "evt-1", "mkt-1", "team-1", "team-1", "WIN", "10.00", "trace-1", "2026-03-04T10:01:00Z");
        publisher.publish(msg);
        verify(rocketMQTemplate).convertAndSend(eq("bet-settlements"), any(BetSettlementMessage.class));
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/bet-matching-service -Dtest=KafkaEventOutcomeListenerTest,RocketMqSettlementPublisherTest test -q`
Expected: FAIL with missing consumer/producer adapters.

**Step 3: Write minimal implementation**

```java
@Service
@RequiredArgsConstructor
class GradeBetsService {
    private final BetJpaRepository repository;
    private final SettlementPublisherPort settlementPublisher;
    private final SettlementMessageMapper mapper;

    void handleOutcome(EventOutcomeMessage outcome) {
        repository.findByEventId(outcome.eventId()).forEach(bet -> {
            var result = bet.getEventWinnerId().equals(outcome.eventWinnerId()) ? "WIN" : "LOSE";
            settlementPublisher.publish(mapper.toMessage(bet, outcome, result));
        });
    }
}
```

```java
@KafkaListener(topics = "event-outcomes", groupId = "bet-matching-service")
public void onMessage(EventOutcomeMessage message) {
    gradeBetsService.handleOutcome(message);
}
```

```java
@Component
@RequiredArgsConstructor
class RocketMqSettlementPublisher implements SettlementPublisherPort {
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void publish(BetSettlementMessage message) {
        rocketMQTemplate.convertAndSend("bet-settlements", message);
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/bet-matching-service -Dtest=KafkaEventOutcomeListenerTest,RocketMqSettlementPublisherTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/bet-matching-service/src/main services/bet-matching-service/src/test
git commit -m "feat: consume outcomes and publish bet settlements"
```

### Task 7: Add Idempotency and Outbox Retry in `bet-matching-service`

**Files:**
- Create: `services/bet-matching-service/src/main/resources/db/migration/V3__create_outbox_and_processed_events.sql`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/persistence/ProcessedEventEntity.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/persistence/SettlementOutboxEntity.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/application/service/OutboxRetryService.java`
- Test: `services/bet-matching-service/src/test/java/com/sporty/matching/application/IdempotencyAndOutboxTest.java`

**Step 1: Write the failing test**

```java
class IdempotencyAndOutboxTest {
    @Test
    void duplicateOutcomeDoesNotCreateDuplicateSettlement() {
        // Arrange same traceId + eventId twice
        // Assert one processed event and one outbox row
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/bet-matching-service -Dtest=IdempotencyAndOutboxTest test -q`
Expected: FAIL with missing tables/services.

**Step 3: Write minimal implementation**

```java
@Transactional
public void handleOutcome(EventOutcomeMessage outcome) {
    if (processedEventRepository.existsByEventIdAndTraceId(outcome.eventId(), outcome.traceId())) {
        return;
    }
    // persist outbox entries and processed marker atomically
}
```

```java
@Scheduled(fixedDelayString = "${app.outbox.retry-delay-ms:5000}")
public void retryPending() {
    outboxRepository.findTop100ByStatus("PENDING")
        .forEach(this::tryPublishAndMark);
}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/bet-matching-service -Dtest=IdempotencyAndOutboxTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/bet-matching-service
git commit -m "feat: add idempotent processing and outbox retry"
```

### Task 8: Implement RocketMQ Consumption and Settlement Persistence in `bet-settlement-service`

**Files:**
- Create: `services/bet-settlement-service/src/main/resources/db/migration/V1__create_settlements_table.sql`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/infrastructure/persistence/SettlementEntity.java`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/infrastructure/persistence/SettlementJpaRepository.java`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/application/service/ApplySettlementService.java`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/infrastructure/messaging/RocketMqSettlementListener.java`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/interfaces/rest/SettlementQueryController.java`
- Test: `services/bet-settlement-service/src/test/java/com/sporty/settlement/application/ApplySettlementServiceTest.java`
- Test: `services/bet-settlement-service/src/test/java/com/sporty/settlement/infrastructure/messaging/RocketMqSettlementListenerTest.java`

**Step 1: Write the failing test**

```java
@ExtendWith(MockitoExtension.class)
class RocketMqSettlementListenerTest {
    @Mock ApplySettlementService applySettlementService;
    @InjectMocks RocketMqSettlementListener listener;

    @Test
    void delegatesSettlementMessageToApplyService() {
        BetSettlementMessage message = new BetSettlementMessage("set-1", "bet-1", "user-1", "evt-1", "mkt-1", "team-1", "team-1", "WIN", "10.00", "trace-1", "2026-03-04T10:01:00Z");
        listener.onMessage(message);
        verify(applySettlementService).apply(message);
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/bet-settlement-service -Dtest=RocketMqSettlementListenerTest,ApplySettlementServiceTest test -q`
Expected: FAIL with missing consumer adapter or settlement service/entity.

**Step 3: Write minimal implementation**

```java
@Service
@RequiredArgsConstructor
class ApplySettlementService {
    private final SettlementJpaRepository repository;

    @Transactional
    public void apply(BetSettlementMessage message) {
        if (repository.existsBySettlementId(message.settlementId())) {
            return;
        }
        repository.save(SettlementEntity.from(message));
    }
}
```

```java
@RocketMQMessageListener(topic = "bet-settlements", consumerGroup = "bet-settlement-service")
class RocketMqSettlementListener implements RocketMQListener<BetSettlementMessage> {
    public void onMessage(BetSettlementMessage message) {
        applySettlementService.apply(message);
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/bet-settlement-service -Dtest=RocketMqSettlementListenerTest,ApplySettlementServiceTest test -q`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/bet-settlement-service
git commit -m "feat: consume settlement commands and persist idempotent settlement state"
```

### Task 9: Add Traceable Structured Logging Across Services

**Files:**
- Create: `services/sports-outcome-service/src/main/java/com/sporty/outcome/infrastructure/logging/TraceIdFilter.java`
- Create: `services/bet-matching-service/src/main/java/com/sporty/matching/infrastructure/logging/TraceIdKafkaInterceptor.java`
- Create: `services/bet-settlement-service/src/main/java/com/sporty/settlement/infrastructure/logging/TraceIdRocketMqHelper.java`
- Modify: `services/*/src/main/resources/application.yml`
- Test: `services/sports-outcome-service/src/test/java/com/sporty/outcome/infrastructure/logging/TraceIdFilterTest.java`

**Step 1: Write the failing test**

```java
class TraceIdFilterTest {
    @Test
    void addsTraceIdWhenHeaderMissing() {
        // execute filter with mock request without X-Trace-Id
        // assert MDC contains traceId and response header contains X-Trace-Id
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./mvnw -pl services/sports-outcome-service -Dtest=TraceIdFilterTest test -q`
Expected: FAIL with missing filter.

**Step 3: Write minimal implementation**

```java
@Component
public class TraceIdFilter extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String traceId = Optional.ofNullable(req.getHeader("X-Trace-Id")).orElse(UUID.randomUUID().toString());
        MDC.put("traceId", traceId);
        res.setHeader("X-Trace-Id", traceId);
        try { chain.doFilter(req, res); } finally { MDC.remove("traceId"); }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./mvnw -pl services/sports-outcome-service,services/bet-matching-service,services/bet-settlement-service test -Dtest=TraceIdFilterTest -q`
Expected: PASS and logging pattern includes `%X{traceId}`.

**Step 5: Commit**

```bash
git add services/*/src/main/java services/*/src/main/resources services/*/src/test
git commit -m "feat: add trace-id propagation and structured logging"
```

### Task 10: Containerize Services and Add Docker Compose Stack

**Files:**
- Create: `services/sports-outcome-service/Dockerfile`
- Create: `services/bet-matching-service/Dockerfile`
- Create: `services/bet-settlement-service/Dockerfile`
- Create: `docker-compose.yml`
- Create: `ops/rocketmq/broker.conf`
- Create: `ops/smoke/smoke-test.sh`
- Test: `ops/smoke/smoke-test.sh`

**Step 1: Write the failing test**

```bash
#!/usr/bin/env bash
set -euo pipefail
curl -fsS http://localhost:8081/actuator/health
curl -fsS http://localhost:8082/actuator/health
curl -fsS http://localhost:8083/actuator/health
```

**Step 2: Run test to verify it fails**

Run: `bash ops/smoke/smoke-test.sh`
Expected: FAIL because services are not containerized/running.

**Step 3: Write minimal implementation**

```yaml
services:
  kafka:
    image: apache/kafka:4.0.0
  rocketmq-namesrv:
    image: apache/rocketmq:5.3.3
  rocketmq-broker:
    image: apache/rocketmq:5.3.3
  sports-outcome-service:
    build: ./services/sports-outcome-service
  bet-matching-service:
    build: ./services/bet-matching-service
  bet-settlement-service:
    build: ./services/bet-settlement-service
```

**Step 4: Run test to verify it passes**

Run: `docker compose up -d --build --wait`
Run: `bash ops/smoke/smoke-test.sh`
Expected: PASS and all three health endpoints return `{"status":"UP"}`.

**Step 5: Commit**

```bash
git add docker-compose.yml ops services/*/Dockerfile
git commit -m "feat: add docker compose environment for kafka rocketmq and services"
```

### Task 11: Document Runbook and Verify End-to-End Flow

**Files:**
- Create: `README.md`
- Create: `ops/smoke/e2e-flow.sh`
- Test: `ops/smoke/e2e-flow.sh`

**Step 1: Write the failing test**

```bash
#!/usr/bin/env bash
set -euo pipefail
TRACE_ID="trace-e2e-001"
curl -fsS -X POST http://localhost:8081/api/v1/event-outcomes \
  -H 'Content-Type: application/json' \
  -H "X-Trace-Id: ${TRACE_ID}" \
  -d '{"eventId":"evt-1","eventName":"Match A","eventWinnerId":"team-1"}'

curl -fsS "http://localhost:8082/api/v1/bets?eventId=evt-1"
curl -fsS "http://localhost:8083/api/v1/settlements?betId=bet-1"
```

**Step 2: Run test to verify it fails**

Run: `bash ops/smoke/e2e-flow.sh`
Expected: FAIL until complete API + messaging + persistence flow is wired.

**Step 3: Write minimal implementation**

```md
## Run
1. `docker compose up -d --build --wait`
2. `bash ops/smoke/e2e-flow.sh`

## Services
- `sports-outcome-service` -> `http://localhost:8081`
- `bet-matching-service` -> `http://localhost:8082`
- `bet-settlement-service` -> `http://localhost:8083`
```

**Step 4: Run test to verify it passes**

Run: `bash ops/smoke/e2e-flow.sh`
Expected: PASS and flow completes from POST outcome to settlement query.

**Step 5: Commit**

```bash
git add README.md ops/smoke
git commit -m "docs: add runbook and end-to-end smoke flow"
```

### Task 12: Final Verification Gate

**Files:**
- Modify: `README.md`
- Create: `ops/verify/all-checks.sh`
- Test: `ops/verify/all-checks.sh`

**Step 1: Write the failing test**

```bash
#!/usr/bin/env bash
set -euo pipefail
./mvnw test
bash ops/smoke/smoke-test.sh
bash ops/smoke/e2e-flow.sh
```

**Step 2: Run test to verify it fails**

Run: `bash ops/verify/all-checks.sh`
Expected: FAIL until all modules and infrastructure are stable.

**Step 3: Write minimal implementation**

```bash
#!/usr/bin/env bash
set -euo pipefail
./mvnw clean test
./mvnw clean package -DskipTests

docker compose up -d --build --wait
bash ops/smoke/smoke-test.sh
bash ops/smoke/e2e-flow.sh

docker compose logs --tail=200 sports-outcome-service bet-matching-service bet-settlement-service
```

**Step 4: Run test to verify it passes**

Run: `bash ops/verify/all-checks.sh`
Expected: PASS with all tests green and smoke scripts passing.

**Step 5: Commit**

```bash
git add ops/verify README.md
git commit -m "chore: add final verification gate"
```
