# Event-Driven Refactor Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Refactor the repo to use shared messaging ports, consolidate mappers by domain, move JPA primary keys to UUID, simplify the event outcome flow, and modernize tests without changing the external business flow.

**Architecture:** Keep the existing three-service event-driven flow, but move service dependencies away from Kafka and RocketMQ implementation classes and toward shared `EventPublisher<T>` contracts in `common-lib`. Consolidate mapping around domain concepts in each service, keep validations inside domain factories, and apply persistence-only UUID changes through JPA entities and Flyway migrations while leaving business IDs as strings.

**Tech Stack:** Java 17, Spring Boot 3.5.11, Spring MVC, Spring Data JPA, Flyway, H2, Spring Kafka, RocketMQ Spring, Lombok, MapStruct 1.6.3, JUnit 5, Mockito, Spring Test, MockMvc.

---

**Skill References:** `@test-driven-development`, `@verification-before-completion`, `@requesting-code-review`

### Task 1: Add Lombok Support and Shared Messaging Ports

**Files:**
- Modify: `pom.xml`
- Modify: `event-outcome-service/pom.xml`
- Modify: `bet-matching-service/pom.xml`
- Modify: `bet-settlement-service/pom.xml`
- Create: `common-lib/src/main/java/com/sporty/groupha/commonlib/messaging/EventPublisher.java`
- Create: `common-lib/src/main/java/com/sporty/groupha/commonlib/messaging/EventListener.java`
- Test: `common-lib/src/test/java/com/sporty/groupha/commonlib/MessageContractSerializationTest.java`

**Step 1: Write the failing test**

Add a focused serialization and compilation-oriented contract test in `MessageContractSerializationTest.java` that references the new contracts.

```java
package com.sporty.groupha.commonlib;

import com.sporty.groupha.commonlib.messaging.EventListener;
import com.sporty.groupha.commonlib.messaging.EventPublisher;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageContractSerializationTest {

    @Test
    void exposesGenericEventMessagingContracts() {
        EventPublisher<EventOutcomeMessage> publisher = event -> { };
        EventListener<EventOutcomeMessage> listener = event -> { };

        assertThat(publisher).isNotNull();
        assertThat(listener).isNotNull();
    }
}
```

**Step 2: Run the test to verify it fails**

Run: `mvn -q -pl common-lib -Dtest=MessageContractSerializationTest test`

Expected: FAIL because `EventPublisher` and `EventListener` do not exist yet.

**Step 3: Write the minimal implementation**

Add the shared contracts:

```java
package com.sporty.groupha.commonlib.messaging;

@FunctionalInterface
public interface EventPublisher<T> {

    void publish(T event);
}
```

```java
package com.sporty.groupha.commonlib.messaging;

@FunctionalInterface
public interface EventListener<T> {

    void onMessage(T event);
}
```

Add Lombok to each service module and annotation processing in the module compiler plugins.

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

```xml
<path>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</path>
```

**Step 4: Run the test to verify it passes**

Run: `mvn -q -pl common-lib -Dtest=MessageContractSerializationTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add pom.xml event-outcome-service/pom.xml bet-matching-service/pom.xml bet-settlement-service/pom.xml common-lib/src/main/java/com/sporty/groupha/commonlib/messaging/EventPublisher.java common-lib/src/main/java/com/sporty/groupha/commonlib/messaging/EventListener.java common-lib/src/test/java/com/sporty/groupha/commonlib/MessageContractSerializationTest.java
git commit -m "refactor: add messaging ports and lombok support"
```

### Task 2: Refactor Event Outcome Domain Validation and Add Test Builders

**Files:**
- Modify: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/domain/EventOutcome.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/domain/EventOutcomeTest.java`
- Create: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeRequestTestBuilder.java`
- Create: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeMessageTestBuilder.java`
- Create: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeEntityTestBuilder.java`

**Step 1: Write the failing tests**

Expand `EventOutcomeTest.java` to cover all branches.

```java
package com.sporty.groupha.eventoutcome.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventOutcomeTest {

    @Test
    void createsEventOutcomeWithOriginalValues() {
        EventOutcome eventOutcome = EventOutcome.create("event-1", "Match A", "winner-1");

        assertThat(eventOutcome.eventId()).isEqualTo("event-1");
        assertThat(eventOutcome.eventName()).isEqualTo("Match A");
        assertThat(eventOutcome.eventWinnerId()).isEqualTo("winner-1");
    }

    @Test
    void rejectsEmptyEventId() {
        assertThatThrownBy(() -> EventOutcome.create("", "Match A", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }

    @Test
    void rejectsEmptyEventName() {
        assertThatThrownBy(() -> EventOutcome.create("event-1", "", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventName");
    }

    @Test
    void rejectsEmptyEventWinnerId() {
        assertThatThrownBy(() -> EventOutcome.create("event-1", "Match A", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventWinnerId");
    }
}
```

**Step 2: Run the tests to verify they fail**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeTest test`

Expected: FAIL because the domain still trims values and only covers one invalid branch.

**Step 3: Write the minimal implementation**

Update the domain factory to keep explicit validation inline and remove the private helper.

```java
package com.sporty.groupha.eventoutcome.domain;

import org.springframework.util.StringUtils;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {

    public static EventOutcome create(String eventId, String eventName, String eventWinnerId) {
        if (StringUtils.isEmpty(eventId)) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (StringUtils.isEmpty(eventName)) {
            throw new IllegalArgumentException("eventName must not be blank");
        }
        if (StringUtils.isEmpty(eventWinnerId)) {
            throw new IllegalArgumentException("eventWinnerId must not be blank");
        }

        return new EventOutcome(eventId, eventName, eventWinnerId);
    }
}
```

Add test builders with sensible defaults and fluent `with...` methods.

```java
package com.sporty.groupha.eventoutcome.support.builders;

import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;

public class EventOutcomeRequestTestBuilder {

    private String eventId = "event-1";
    private String eventName = "Match A";
    private String eventWinnerId = "winner-1";

    public EventOutcomeRequestTestBuilder withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventOutcomeRequest build() {
        return new EventOutcomeRequest(eventId, eventName, eventWinnerId);
    }
}
```

Mirror the same style for `EventOutcomeMessageTestBuilder` and `EventOutcomeEntityTestBuilder`.

**Step 4: Run the tests to verify they pass**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/domain/EventOutcome.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/domain/EventOutcomeTest.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeRequestTestBuilder.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeMessageTestBuilder.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeEntityTestBuilder.java
git commit -m "test: cover event outcome validation and add builders"
```

### Task 3: Refactor Event Outcome Controller, Service, Mapper, and Publisher Boundary

**Files:**
- Modify: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeController.java`
- Delete: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeCommand.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeService.java`
- Delete: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeApiMapper.java`
- Delete: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeMessageMapper.java`
- Delete: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/AcceptedEventOutcomeEntityMapper.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeMapper.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisher.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeControllerTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeServiceTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisherTest.java`

**Step 1: Write the failing tests**

Update the controller test to use the domain mapper and the service signature that accepts `EventOutcome`.

```java
package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class EventOutcomeControllerTest {

    @MockitoBean
    private EventOutcomeMapper eventOutcomeMapper;

    @Test
    void mapsRequestAndDelegatesToService() throws Exception {
        EventOutcome eventOutcome = EventOutcome.create("event-1", "Match A", "winner-1");
        given(eventOutcomeMapper.toDomain(any())).willReturn(eventOutcome);

        // perform request

        verify(publishEventOutcomeService).publish(eventOutcome);
    }
}
```

Update the service test to verify it saves the entity and publishes through `EventPublisher<EventOutcomeMessage>`.

```java
@Mock
private EventPublisher<EventOutcomeMessage> eventPublisher;
```

**Step 2: Run the tests to verify they fail**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeControllerTest,PublishEventOutcomeServiceTest,KafkaEventOutcomePublisherTest test`

Expected: FAIL because the controller still uses `PublishEventOutcomeCommand`, the consolidated mapper does not exist, and the service still depends on `KafkaEventOutcomePublisher`.

**Step 3: Write the minimal implementation**

Create the consolidated mapper.

```java
package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMapper {

    EventOutcome toDomain(EventOutcomeRequest eventOutcomeRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventOutcomeEntity toEntity(EventOutcome eventOutcome);

    EventOutcomeMessage toMessage(EventOutcome eventOutcome);
}
```

Refactor the controller and service.

```java
@RestController
@RequestMapping("/api/event-outcomes")
@RequiredArgsConstructor
public class EventOutcomeController {

    private final PublishEventOutcomeService publishEventOutcomeService;
    private final EventOutcomeMapper eventOutcomeMapper;

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody EventOutcomeRequest eventOutcomeRequest) {
        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeRequest);
        publishEventOutcomeService.publish(eventOutcome);
        return ResponseEntity.accepted().build();
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class PublishEventOutcomeService {

    private final EventOutcomeJpaRepository eventOutcomeJpaRepository;
    private final EventOutcomeMapper eventOutcomeMapper;
    private final EventPublisher<EventOutcomeMessage> eventPublisher;

    public void publish(EventOutcome eventOutcome) {
        EventOutcomeEntity eventOutcomeEntity = eventOutcomeMapper.toEntity(eventOutcome);
        EventOutcomeMessage eventOutcomeMessage = eventOutcomeMapper.toMessage(eventOutcome);

        eventOutcomeJpaRepository.save(eventOutcomeEntity);
        eventPublisher.publish(eventOutcomeMessage);
    }
}
```

Make the adapter implement the shared port.

```java
@Component
@RequiredArgsConstructor
public class KafkaEventOutcomePublisher implements EventPublisher<EventOutcomeMessage> {

    private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;
    @Value("${app.messaging.event-outcomes-topic}")
    private String topicName;

    @Override
    public void publish(EventOutcomeMessage eventOutcomeMessage) {
        kafkaTemplate.send(topicName, eventOutcomeMessage);
    }
}
```

If field injection feels undesirable, keep constructor injection for `topicName`; the key requirement is that the adapter implements `EventPublisher<EventOutcomeMessage>` while the service depends only on the port.

**Step 4: Run the tests to verify they pass**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeControllerTest,PublishEventOutcomeServiceTest,KafkaEventOutcomePublisherTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeController.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeService.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeMapper.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisher.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeControllerTest.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeServiceTest.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisherTest.java
git rm event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeCommand.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeApiMapper.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeMessageMapper.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/AcceptedEventOutcomeEntityMapper.java
git commit -m "refactor: simplify event outcome publishing flow"
```

### Task 4: Rename Event Outcome Persistence Types and Move the JPA Primary Key to UUID

**Files:**
- Delete: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/entity/AcceptedEventOutcomeEntity.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/entity/EventOutcomeEntity.java`
- Delete: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/AcceptedEventOutcomeJpaRepository.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/EventOutcomeJpaRepository.java`
- Modify: `event-outcome-service/src/main/resources/db/migration/V1__create_accepted_event_outcomes.sql`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/AcceptedEventOutcomeJpaRepositoryTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeEntityTestBuilder.java`

**Step 1: Write the failing test**

Rename the repository test to `EventOutcomeJpaRepositoryTest` and assert that the persisted entity gets a UUID primary key.

```java
package com.sporty.groupha.eventoutcome.infrastructure.persistence;

import com.sporty.groupha.eventoutcome.infrastructure.entity.EventOutcomeEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventOutcomeJpaRepositoryTest {

    @Test
    void savesEventOutcomeWithGeneratedUuidId() {
        EventOutcomeEntity eventOutcomeEntity = new EventOutcomeEntityTestBuilder().build();

        EventOutcomeEntity savedEventOutcomeEntity = eventOutcomeJpaRepository.save(eventOutcomeEntity);

        assertThat(savedEventOutcomeEntity.getId()).isNotNull();
    }
}
```

**Step 2: Run the test to verify it fails**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeJpaRepositoryTest test`

Expected: FAIL because the old entity and repository names still exist and the primary key is numeric.

**Step 3: Write the minimal implementation**

Create the renamed entity with Lombok getters and a UUID primary key.

```java
package com.sporty.groupha.eventoutcome.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accepted_event_outcomes")
@Getter
@Setter
public class EventOutcomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_winner_id", nullable = false)
    private String eventWinnerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
```

Update the repository signature.

```java
public interface EventOutcomeJpaRepository extends JpaRepository<EventOutcomeEntity, UUID> {
}
```

Update the Flyway migration so the primary key column is UUID-compatible for H2.

```sql
create table accepted_event_outcomes (
    id uuid primary key,
    event_id varchar(255) not null,
    event_name varchar(255) not null,
    event_winner_id varchar(255) not null,
    created_at timestamp not null
);
```

**Step 4: Run the test to verify it passes**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeJpaRepositoryTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/entity/EventOutcomeEntity.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/EventOutcomeJpaRepository.java event-outcome-service/src/main/resources/db/migration/V1__create_accepted_event_outcomes.sql event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/AcceptedEventOutcomeJpaRepositoryTest.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/support/builders/EventOutcomeEntityTestBuilder.java
git rm event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/entity/AcceptedEventOutcomeEntity.java event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/AcceptedEventOutcomeJpaRepository.java
git commit -m "refactor: rename event outcome persistence model"
```

### Task 5: Refactor Bet Matching Messaging Boundaries, Mapper Names, and Stream Processing

**Files:**
- Modify: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeService.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/messaging/EventOutcomeKafkaListener.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisher.java`
- Delete: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/EventOutcomeMessageMapper.java`
- Delete: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetSettlementMessageMapper.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/EventOutcomeMapper.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetSettlementMapper.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeServiceTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/messaging/EventOutcomeKafkaListenerTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisherTest.java`
- Create: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/EventOutcomeTestBuilder.java`
- Create: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/BetSettlementMessageTestBuilder.java`

**Step 1: Write the failing tests**

Update `ProcessEventOutcomeServiceTest` to verify publishing through `EventPublisher<BetSettlementMessage>` and to keep builder-based setup.

```java
@Mock
private EventPublisher<BetSettlementMessage> eventPublisher;

@Test
void publishesOneSettlementMessagePerMatchedBet() {
    EventOutcome eventOutcome = new EventOutcomeTestBuilder().build();
    BetEntity betEntity = new BetEntityTestBuilder().build();
    Bet bet = new BetTestBuilder().build();
    BetSettlementMessage betSettlementMessage = new BetSettlementMessageTestBuilder().build();

    given(betJpaRepository.findByEventId("event-1")).willReturn(List.of(betEntity));
    given(betMapper.toDomain(betEntity)).willReturn(bet);
    given(betSettlementMapper.toMessage(any())).willReturn(betSettlementMessage);

    processEventOutcomeService.process(eventOutcome);

    verify(eventPublisher).publish(betSettlementMessage);
}
```

Update the listener test to assert delegation through the consolidated `EventOutcomeMapper`.

**Step 2: Run the tests to verify they fail**

Run: `mvn -q -pl bet-matching-service -Dtest=ProcessEventOutcomeServiceTest,EventOutcomeKafkaListenerTest,RocketMqBetSettlementPublisherTest test`

Expected: FAIL because the service still depends on `RocketMqBetSettlementPublisher`, the mappers still use the old split names, and the service still uses an imperative loop.

**Step 3: Write the minimal implementation**

Refactor the service to use the port and streams.

```java
@Service
@RequiredArgsConstructor
public class ProcessEventOutcomeService {

    private final BetJpaRepository betJpaRepository;
    private final BetMapper betMapper;
    private final BetSettlementMapper betSettlementMapper;
    private final EventPublisher<BetSettlementMessage> eventPublisher;

    public void process(EventOutcome eventOutcome) {
        String eventId = eventOutcome.eventId();
        List<BetEntity> matchedBetEntities = betJpaRepository.findByEventId(eventId);

        matchedBetEntities.stream()
                .map(betMapper::toDomain)
                .map(matchedBet -> matchedBet.settleAgainst(eventOutcome))
                .map(betSettlementMapper::toMessage)
                .forEach(eventPublisher::publish);
    }
}
```

Create the consolidated mappers.

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMapper {

    EventOutcome toDomain(EventOutcomeMessage eventOutcomeMessage);
}
```

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    BetSettlementMessage toMessage(SettlementDecision settlementDecision);
}
```

Update the adapters to the shared contracts.

```java
@Component
@RequiredArgsConstructor
public class EventOutcomeKafkaListener implements EventListener<EventOutcomeMessage> {

    private final EventOutcomeMapper eventOutcomeMapper;
    private final ProcessEventOutcomeService processEventOutcomeService;

    @Override
    @KafkaListener(topics = "${app.messaging.event-outcomes-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(EventOutcomeMessage eventOutcomeMessage) {
        EventOutcome eventOutcome = eventOutcomeMapper.toDomain(eventOutcomeMessage);
        processEventOutcomeService.process(eventOutcome);
    }
}
```

```java
@Component
@RequiredArgsConstructor
public class RocketMqBetSettlementPublisher implements EventPublisher<BetSettlementMessage> {

    private final RocketMQTemplate rocketMQTemplate;
    private final String topicName;

    @Override
    public void publish(BetSettlementMessage betSettlementMessage) {
        rocketMQTemplate.syncSend(topicName, betSettlementMessage);
    }
}
```

Keep constructor injection for `topicName` if Lombok cannot express the `@Value` argument cleanly in this adapter.

**Step 4: Run the tests to verify they pass**

Run: `mvn -q -pl bet-matching-service -Dtest=ProcessEventOutcomeServiceTest,EventOutcomeKafkaListenerTest,RocketMqBetSettlementPublisherTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add bet-matching-service/src/main/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeService.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/messaging/EventOutcomeKafkaListener.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisher.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/EventOutcomeMapper.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetSettlementMapper.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeServiceTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/messaging/EventOutcomeKafkaListenerTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisherTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/EventOutcomeTestBuilder.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/BetSettlementMessageTestBuilder.java
git rm bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/EventOutcomeMessageMapper.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetSettlementMessageMapper.java
git commit -m "refactor: decouple bet matching from broker adapters"
```

### Task 6: Add Surrogate UUID Primary Key and Generated MapStruct Mapper to Bet Matching

**Files:**
- Modify: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/entity/BetEntity.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/persistence/BetJpaRepository.java`
- Modify: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetEntityMapper.java`
- Modify: `bet-matching-service/src/main/resources/db/migration/V1__create_bets.sql`
- Modify: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/persistence/BetJpaRepositoryTest.java`
- Create: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/BetEntityTestBuilder.java`
- Create: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/BetTestBuilder.java`

**Step 1: Write the failing test**

Update the repository test to assert that the entity gets a generated UUID primary key and still supports lookup by business `eventId`.

```java
@Test
void savesBetWithGeneratedUuidPrimaryKey() {
    BetEntity betEntity = new BetEntityTestBuilder().build();

    BetEntity savedBetEntity = betJpaRepository.save(betEntity);

    assertThat(savedBetEntity.getId()).isNotNull();
    assertThat(savedBetEntity.getBetId()).isEqualTo("bet-1");
}
```

**Step 2: Run the test to verify it fails**

Run: `mvn -q -pl bet-matching-service -Dtest=BetJpaRepositoryTest test`

Expected: FAIL because `BetEntity` still uses `betId` as the JPA primary key.

**Step 3: Write the minimal implementation**

Move the JPA primary key to a UUID surrogate key and keep `betId` as a unique business field.

```java
@Entity
@Table(name = "bets")
@Getter
@Setter
public class BetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bet_id", nullable = false, unique = true)
    private String betId;

    // other fields unchanged
}
```

Update the repository signature.

```java
public interface BetJpaRepository extends JpaRepository<BetEntity, UUID> {

    List<BetEntity> findByEventId(String eventId);
}
```

Convert `BetEntityMapper` to generated MapStruct code.

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetMapper {

    @Mapping(target = "betId", source = "betId")
    Bet toDomain(BetEntity betEntity);
}
```

Update the Flyway migration.

```sql
create table bets (
    id uuid primary key,
    bet_id varchar(255) not null unique,
    user_id varchar(255) not null,
    event_id varchar(255) not null,
    event_market_id varchar(255) not null,
    event_winner_id varchar(255) not null,
    bet_amount decimal(19,2) not null
);
```

**Step 4: Run the tests to verify they pass**

Run: `mvn -q -pl bet-matching-service -Dtest=BetJpaRepositoryTest,ProcessEventOutcomeServiceTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/entity/BetEntity.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/persistence/BetJpaRepository.java bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetEntityMapper.java bet-matching-service/src/main/resources/db/migration/V1__create_bets.sql bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/persistence/BetJpaRepositoryTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/BetEntityTestBuilder.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/support/builders/BetTestBuilder.java
git commit -m "refactor: add uuid primary key to bets"
```

### Task 7: Refactor Bet Settlement Listener, Mapper Layout, and UUID Persistence

**Files:**
- Modify: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/messaging/BetSettlementRocketMqListener.java`
- Modify: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementService.java`
- Delete: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementEntityMapper.java`
- Delete: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementMessageMapper.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementMapper.java`
- Modify: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/entity/BetSettlementEntity.java`
- Modify: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/persistence/BetSettlementJpaRepository.java`
- Modify: `bet-settlement-service/src/main/resources/db/migration/V1__create_bet_settlements.sql`
- Modify: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/infrastructure/messaging/BetSettlementRocketMqListenerTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementServiceTest.java`
- Create: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/support/builders/BetSettlementMessageTestBuilder.java`
- Create: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/support/builders/BetSettlementEntityTestBuilder.java`
- Create: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/support/builders/BetSettlementTestBuilder.java`

**Step 1: Write the failing tests**

Update the listener test to use the consolidated mapper and builder-based setup.

```java
@Test
void mapsIncomingMessageAndDelegatesToApplyService() {
    BetSettlementMessage betSettlementMessage = new BetSettlementMessageTestBuilder().build();
    BetSettlement betSettlement = new BetSettlementTestBuilder().build();

    given(betSettlementMapper.toDomain(betSettlementMessage)).willReturn(betSettlement);

    betSettlementRocketMqListener.onMessage(betSettlementMessage);

    verify(applyBetSettlementService).apply(betSettlement);
}
```

Update the persistence-facing service test to assert a UUID primary key on `BetSettlementEntity`.

**Step 2: Run the tests to verify they fail**

Run: `mvn -q -pl bet-settlement-service -Dtest=BetSettlementRocketMqListenerTest,ApplyBetSettlementServiceTest test`

Expected: FAIL because the split mappers still exist and the entity still uses `betId` as the JPA primary key.

**Step 3: Write the minimal implementation**

Create the consolidated mapper.

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    BetSettlement toDomain(BetSettlementMessage betSettlementMessage);

    @Mapping(target = "id", ignore = true)
    BetSettlementEntity toEntity(BetSettlement betSettlement);
}
```

Refactor the listener to the shared contract.

```java
@Service
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${app.messaging.bet-settlements-topic}",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class BetSettlementRocketMqListener implements RocketMQListener<BetSettlementMessage>, EventListener<BetSettlementMessage> {

    private final BetSettlementMapper betSettlementMapper;
    private final ApplyBetSettlementService applyBetSettlementService;

    @Override
    public void onMessage(BetSettlementMessage betSettlementMessage) {
        BetSettlement betSettlement = betSettlementMapper.toDomain(betSettlementMessage);
        applyBetSettlementService.apply(betSettlement);
    }
}
```

Move the entity primary key to a surrogate UUID.

```java
@Entity
@Table(name = "bet_settlements")
@Getter
@Setter
public class BetSettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bet_id", nullable = false, unique = true, length = 64)
    private String betId;

    // other fields unchanged
}
```

Update the repository signature and Flyway migration accordingly.

```sql
create table bet_settlements (
    id uuid primary key,
    bet_id varchar(64) not null unique,
    user_id varchar(64) not null,
    event_id varchar(64) not null,
    event_market_id varchar(64) not null,
    expected_winner_id varchar(64) not null,
    actual_winner_id varchar(64) not null,
    bet_amount decimal(19, 2) not null,
    result varchar(16) not null
);
```

**Step 4: Run the tests to verify they pass**

Run: `mvn -q -pl bet-settlement-service -Dtest=BetSettlementRocketMqListenerTest,ApplyBetSettlementServiceTest test`

Expected: PASS

**Step 5: Commit**

```bash
git add bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/messaging/BetSettlementRocketMqListener.java bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementService.java bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementMapper.java bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/entity/BetSettlementEntity.java bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/persistence/BetSettlementJpaRepository.java bet-settlement-service/src/main/resources/db/migration/V1__create_bet_settlements.sql bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/infrastructure/messaging/BetSettlementRocketMqListenerTest.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementServiceTest.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/support/builders/BetSettlementMessageTestBuilder.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/support/builders/BetSettlementEntityTestBuilder.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/support/builders/BetSettlementTestBuilder.java
git rm bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementEntityMapper.java bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementMessageMapper.java
git commit -m "refactor: simplify bet settlement ingestion"
```

### Task 8: Remove Deprecated Test Patterns, Delete Context Tests, and Run Full Verification

**Files:**
- Delete: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/ApplicationContextTest.java`
- Delete: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/ApplicationContextTest.java`
- Delete: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/ApplicationContextTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeControllerTest.java`
- Modify: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeServiceTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeServiceTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementServiceTest.java`
- Modify: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/domain/BetTest.java`
- Modify: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/domain/BetSettlementTest.java`

**Step 1: Write the failing tests**

Update all Spring test slices to use `@MockitoBean` instead of `@MockBean`, remove direct constructor setup from the remaining tests, and replace it with the builders created in earlier tasks.

Representative controller test field update:

```java
@MockitoBean
private PublishEventOutcomeService publishEventOutcomeService;

@MockitoBean
private EventOutcomeMapper eventOutcomeMapper;
```

Representative domain test update:

```java
@Test
void returnsWonSettlementWhenWinnerMatches() {
    Bet bet = new BetTestBuilder().withEventWinnerId("winner-1").build();
    EventOutcome eventOutcome = new EventOutcomeTestBuilder().withEventWinnerId("winner-1").build();

    SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

    assertThat(settlementDecision.result()).isEqualTo(SettlementResult.WON);
}
```

**Step 2: Run the tests to verify they fail**

Run: `mvn -q test`

Expected: FAIL until all context tests are removed, imports are normalized, and test builders replace the remaining raw object construction.

**Step 3: Write the minimal implementation**

Perform the final cleanup:
- delete the three `ApplicationContextTest.java` files,
- replace remaining `@MockBean` imports and annotations with `@MockitoBean`,
- replace remaining raw test construction with builders,
- normalize imports so no inline fully qualified imports remain in test code,
- keep constructor and field usage explicit and readable.

**Step 4: Run the tests to verify they pass**

Run: `mvn -q test`

Expected: PASS for the full multi-module build.

Run: `git diff --check`

Expected: no whitespace or conflict-marker issues.

**Step 5: Commit**

```bash
git add event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeControllerTest.java event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeServiceTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeServiceTest.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementServiceTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/domain/BetTest.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/domain/BetSettlementTest.java
git rm event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/ApplicationContextTest.java bet-matching-service/src/test/java/com/sporty/groupha/betmatching/ApplicationContextTest.java bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/ApplicationContextTest.java
git commit -m "test: modernize test support and cleanup"
```

### Task 9: Final Verification and Review Preparation

**Files:**
- Modify: no new source files expected
- Verify: the full working tree

**Step 1: Run focused module verification**

Run: `mvn -q -pl event-outcome-service -Dtest=EventOutcomeTest,EventOutcomeControllerTest,EventOutcomeJpaRepositoryTest,PublishEventOutcomeServiceTest,KafkaEventOutcomePublisherTest test`

Expected: PASS

Run: `mvn -q -pl bet-matching-service -Dtest=BetTest,BetJpaRepositoryTest,EventOutcomeKafkaListenerTest,RocketMqBetSettlementPublisherTest,ProcessEventOutcomeServiceTest test`

Expected: PASS

Run: `mvn -q -pl bet-settlement-service -Dtest=BetSettlementTest,BetSettlementRocketMqListenerTest,ApplyBetSettlementServiceTest test`

Expected: PASS

**Step 2: Run full build verification**

Run: `mvn -q test`

Expected: PASS

Run: `git diff --check`

Expected: no output

Run: `git status --short`

Expected: clean working tree

**Step 3: Request code review**

Use `@requesting-code-review` and ask for a review focused on:
- message boundary abstraction correctness,
- UUID JPA key migration safety,
- mapper consolidation completeness,
- test builder adoption coverage,
- any regressions from stream conversions.

**Step 4: Commit any review-driven cleanup**

```bash
git commit -am "refactor: finalize event-driven cleanup"
```

Only run this step if the review produces small follow-up edits that are not already committed.
