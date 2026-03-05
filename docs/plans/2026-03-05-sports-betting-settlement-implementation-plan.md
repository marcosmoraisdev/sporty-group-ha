# Sports Betting Settlement Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build three executable Spring Boot microservices that ingest event outcomes over HTTP, publish them to Kafka, match bets from H2, publish settlement commands to RocketMQ, and persist final settlements.

**Architecture:** Use a pragmatic event-driven microservices design in a Maven mono-repo with four modules: `common-lib`, `event-outcome-service`, `bet-matching-service`, and `bet-settlement-service`. Each service uses `domain`, `services`, and `infrastructure` packages, keeps business rules inside domain models, uses MapStruct for all boundary mappings, and owns its own H2 + Flyway setup.

**Tech Stack:** Java 17, Spring Boot 3.5.11, Spring Web, Spring Validation, Spring Data JPA, Flyway, H2, Spring Kafka, RocketMQ Spring, MapStruct 1.6.3, Maven Wrapper, JUnit 5, Mockito, AssertJ, MockMvc, Docker Compose.

---

**Skill References:** `@test-driven-development`, `@verification-before-completion`, `@requesting-code-review`

### Task 1: Bootstrap the Mono-Repo Build and Service Skeletons

**Files:**
- Create: `pom.xml`
- Create: `.mvn/wrapper/maven-wrapper.properties`
- Create: `mvnw`
- Create: `mvnw.cmd`
- Create: `common-lib/pom.xml`
- Create: `event-outcome-service/pom.xml`
- Create: `bet-matching-service/pom.xml`
- Create: `bet-settlement-service/pom.xml`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/EventOutcomeServiceApplication.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/BetMatchingServiceApplication.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/BetSettlementServiceApplication.java`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/ApplicationContextTest.java`
- Test: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/ApplicationContextTest.java`
- Test: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/ApplicationContextTest.java`

**Step 1: Write the failing tests**

```java
package com.sporty.groupha.eventoutcome;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationContextTest {

    @Test
    void contextLoads() {
    }
}
```

Duplicate the same shape for `bet-matching-service` and `bet-settlement-service` with matching package names.

**Step 2: Run the tests to verify they fail**

Run: `mvn -q -pl event-outcome-service -Dtest=ApplicationContextTest test`
Expected: FAIL because the parent POM and application classes do not exist yet.

**Step 3: Write the minimal implementation**

Root `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.11</version>
        <relativePath/>
    </parent>

    <groupId>com.sporty.groupha</groupId>
    <artifactId>sporty-group-ha</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>

    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.6.3</mapstruct.version>
        <rocketmq-spring.version>2.3.4</rocketmq-spring.version>
    </properties>

    <modules>
        <module>common-lib</module>
        <module>event-outcome-service</module>
        <module>bet-matching-service</module>
        <module>bet-settlement-service</module>
    </modules>
</project>
```

`common-lib/pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.sporty.groupha</groupId>
        <artifactId>sporty-group-ha</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>common-lib</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

`event-outcome-service/pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.sporty.groupha</groupId>
        <artifactId>sporty-group-ha</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>event-outcome-service</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.sporty.groupha</groupId>
            <artifactId>common-lib</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <release>${java.version}</release>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

Create `bet-matching-service/pom.xml` from the same template, adding:

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>${rocketmq-spring.version}</version>
</dependency>
```

alongside `spring-kafka`, JPA, Flyway, H2, MapStruct, and `common-lib`.

Create `bet-settlement-service/pom.xml` from the same template, adding the same RocketMQ Spring dependency plus JPA, Flyway, H2, MapStruct, and `common-lib`.

`EventOutcomeServiceApplication.java`:

```java
package com.sporty.groupha.eventoutcome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventOutcomeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventOutcomeServiceApplication.class, args);
    }
}
```

Create equivalent application classes for the other two services.

Create the Maven wrapper with:

```bash
mvn -N wrapper:wrapper
chmod +x mvnw
```

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl event-outcome-service,bet-matching-service,bet-settlement-service -Dtest=ApplicationContextTest test`
Expected: PASS for all three modules.

**Step 5: Commit**

```bash
git add pom.xml .mvn mvnw mvnw.cmd common-lib event-outcome-service bet-matching-service bet-settlement-service
git commit -m "chore: bootstrap multi-module microservices workspace"
```

### Task 2: Add the Shared Message Contracts Module

**Files:**
- Modify: `common-lib/pom.xml`
- Create: `common-lib/src/main/java/com/sporty/groupha/commonlib/messaging/event/EventOutcomeMessage.java`
- Create: `common-lib/src/main/java/com/sporty/groupha/commonlib/messaging/settlement/BetSettlementMessage.java`
- Test: `common-lib/src/test/java/com/sporty/groupha/commonlib/MessageContractSerializationTest.java`

**Step 1: Write the failing test**

```java
package com.sporty.groupha.commonlib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageContractSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesEventOutcomeMessage() throws Exception {
        EventOutcomeMessage message = new EventOutcomeMessage("event-1", "Arsenal vs Benfica", "team-7");

        String json = objectMapper.writeValueAsString(message);

        assertThat(json)
                .contains("event-1")
                .contains("Arsenal vs Benfica")
                .contains("team-7");
    }
}
```

**Step 2: Run the test to verify it fails**

Run: `./mvnw -q -pl common-lib -Dtest=MessageContractSerializationTest test`
Expected: FAIL because the message records do not exist.

**Step 3: Write the minimal implementation**

`EventOutcomeMessage.java`:

```java
package com.sporty.groupha.commonlib.messaging.event;

public record EventOutcomeMessage(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
```

`BetSettlementMessage.java`:

```java
package com.sporty.groupha.commonlib.messaging.settlement;

import java.math.BigDecimal;

public record BetSettlementMessage(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        String result
) {
}
```

**Step 4: Run the test to verify it passes**

Run: `./mvnw -q -pl common-lib -Dtest=MessageContractSerializationTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add common-lib
git commit -m "feat: add shared message contracts"
```

### Task 3: Implement Event Outcome Domain Validation and HTTP Ingestion

**Files:**
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/domain/EventOutcome.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeCommand.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeService.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeController.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeExceptionHandler.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/dto/EventOutcomeRequest.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeApiMapper.java`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/domain/EventOutcomeTest.java`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/controller/EventOutcomeControllerTest.java`

**Step 1: Write the failing tests**

`EventOutcomeTest.java`:

```java
package com.sporty.groupha.eventoutcome.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventOutcomeTest {

    @Test
    void rejectsBlankEventId() {
        assertThatThrownBy(() -> EventOutcome.create(" ", "Match A", "winner-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }
}
```

`EventOutcomeControllerTest.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeService;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeApiMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublishEventOutcomeService publishEventOutcomeService;

    @MockBean
    private EventOutcomeApiMapper eventOutcomeApiMapper;

    @Test
    void returnsAcceptedForValidPayload() throws Exception {
        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"eventId":"event-1","eventName":"Match A","eventWinnerId":"winner-1"}
                                """))
                .andExpect(status().isAccepted());
    }
}
```

**Step 2: Run the tests to verify they fail**

Run: `./mvnw -q -pl event-outcome-service -Dtest=EventOutcomeTest,EventOutcomeControllerTest test`
Expected: FAIL because the domain model, mapper, controller, and service do not exist.

**Step 3: Write the minimal implementation**

`EventOutcome.java`:

```java
package com.sporty.groupha.eventoutcome.domain;

public record EventOutcome(
        String eventId,
        String eventName,
        String eventWinnerId
) {

    public static EventOutcome create(String eventId, String eventName, String eventWinnerId) {
        validate("eventId", eventId);
        validate("eventName", eventName);
        validate("eventWinnerId", eventWinnerId);
        return new EventOutcome(eventId.trim(), eventName.trim(), eventWinnerId.trim());
    }

    private static void validate(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
```

`EventOutcomeRequest.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;

public record EventOutcomeRequest(
        @NotBlank String eventId,
        @NotBlank String eventName,
        @NotBlank String eventWinnerId
) {
}
```

`PublishEventOutcomeCommand.java`:

```java
package com.sporty.groupha.eventoutcome.services;

public record PublishEventOutcomeCommand(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}
```

`EventOutcomeApiMapper.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeApiMapper {

    PublishEventOutcomeCommand toCommand(EventOutcomeRequest request);
}
```

`EventOutcomeController.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.controller;

import com.sporty.groupha.eventoutcome.infrastructure.dto.EventOutcomeRequest;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.EventOutcomeApiMapper;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeCommand;
import com.sporty.groupha.eventoutcome.services.PublishEventOutcomeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-outcomes")
public class EventOutcomeController {

    private final PublishEventOutcomeService publishEventOutcomeService;
    private final EventOutcomeApiMapper eventOutcomeApiMapper;

    public EventOutcomeController(
            PublishEventOutcomeService publishEventOutcomeService,
            EventOutcomeApiMapper eventOutcomeApiMapper
    ) {
        this.publishEventOutcomeService = publishEventOutcomeService;
        this.eventOutcomeApiMapper = eventOutcomeApiMapper;
    }

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody EventOutcomeRequest request) {
        PublishEventOutcomeCommand publishEventOutcomeCommand = eventOutcomeApiMapper.toCommand(request);

        publishEventOutcomeService.publish(publishEventOutcomeCommand);
        return ResponseEntity.accepted().build();
    }
}
```

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl event-outcome-service -Dtest=EventOutcomeTest,EventOutcomeControllerTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add event-outcome-service
git commit -m "feat: add event outcome domain and ingestion api"
```

### Task 4: Persist Accepted Event Outcomes with H2 and Flyway

**Files:**
- Create: `event-outcome-service/src/main/resources/db/migration/V1__create_accepted_event_outcomes.sql`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/entity/AcceptedEventOutcomeEntity.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/AcceptedEventOutcomeJpaRepository.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/AcceptedEventOutcomeEntityMapper.java`
- Modify: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeService.java`
- Create: `event-outcome-service/src/main/resources/application.yml`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeServiceTest.java`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/persistence/AcceptedEventOutcomeJpaRepositoryTest.java`

**Step 1: Write the failing tests**

`PublishEventOutcomeServiceTest.java`:

```java
package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PublishEventOutcomeServiceTest {

    @Mock
    private AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;

    @InjectMocks
    private PublishEventOutcomeService publishEventOutcomeService;

    @Test
    void savesAcceptedOutcomeBeforePublishing() {
        PublishEventOutcomeCommand publishEventOutcomeCommand =
                new PublishEventOutcomeCommand("event-1", "Match A", "winner-1");

        publishEventOutcomeService.publish(publishEventOutcomeCommand);

        verify(acceptedEventOutcomeJpaRepository).save(org.mockito.ArgumentMatchers.any());
    }
}
```

`AcceptedEventOutcomeJpaRepositoryTest.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.persistence;

import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AcceptedEventOutcomeJpaRepositoryTest {

    @Autowired
    private AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;

    @Test
    void savesEntity() {
        AcceptedEventOutcomeEntity entity = new AcceptedEventOutcomeEntity();
        entity.setEventId("event-1");
        entity.setEventName("Match A");
        entity.setEventWinnerId("winner-1");

        AcceptedEventOutcomeEntity savedEntity = acceptedEventOutcomeJpaRepository.save(entity);

        assertThat(savedEntity.getId()).isNotNull();
    }
}
```

**Step 2: Run the tests to verify they fail**

Run: `./mvnw -q -pl event-outcome-service -Dtest=PublishEventOutcomeServiceTest,AcceptedEventOutcomeJpaRepositoryTest test`
Expected: FAIL because the entity, repository, migration, and service logic do not exist.

**Step 3: Write the minimal implementation**

`V1__create_accepted_event_outcomes.sql`:

```sql
create table accepted_event_outcomes (
    id bigint generated by default as identity primary key,
    event_id varchar(255) not null,
    event_name varchar(255) not null,
    event_winner_id varchar(255) not null,
    created_at timestamp not null
);
```

`PublishEventOutcomeService.java`:

```java
package com.sporty.groupha.eventoutcome.services;

import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import com.sporty.groupha.eventoutcome.infrastructure.entity.AcceptedEventOutcomeEntity;
import com.sporty.groupha.eventoutcome.infrastructure.mappers.AcceptedEventOutcomeEntityMapper;
import com.sporty.groupha.eventoutcome.infrastructure.persistence.AcceptedEventOutcomeJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PublishEventOutcomeService {

    private final AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository;
    private final AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper;

    public PublishEventOutcomeService(
            AcceptedEventOutcomeJpaRepository acceptedEventOutcomeJpaRepository,
            AcceptedEventOutcomeEntityMapper acceptedEventOutcomeEntityMapper
    ) {
        this.acceptedEventOutcomeJpaRepository = acceptedEventOutcomeJpaRepository;
        this.acceptedEventOutcomeEntityMapper = acceptedEventOutcomeEntityMapper;
    }

    public void publish(PublishEventOutcomeCommand publishEventOutcomeCommand) {
        EventOutcome eventOutcome = EventOutcome.create(
                publishEventOutcomeCommand.eventId(),
                publishEventOutcomeCommand.eventName(),
                publishEventOutcomeCommand.eventWinnerId()
        );
        AcceptedEventOutcomeEntity acceptedEventOutcomeEntity = acceptedEventOutcomeEntityMapper.toEntity(eventOutcome);

        acceptedEventOutcomeJpaRepository.save(acceptedEventOutcomeEntity);
    }
}
```

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl event-outcome-service -Dtest=PublishEventOutcomeServiceTest,AcceptedEventOutcomeJpaRepositoryTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add event-outcome-service
git commit -m "feat: persist accepted event outcomes"
```

### Task 5: Publish Event Outcomes to Kafka

**Files:**
- Modify: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeService.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/mappers/EventOutcomeMessageMapper.java`
- Create: `event-outcome-service/src/main/java/com/sporty/groupha/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisher.java`
- Modify: `event-outcome-service/src/main/resources/application.yml`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/infrastructure/messaging/KafkaEventOutcomePublisherTest.java`
- Test: `event-outcome-service/src/test/java/com/sporty/groupha/eventoutcome/services/PublishEventOutcomeServiceTest.java`

**Step 1: Write the failing tests**

`KafkaEventOutcomePublisherTest.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventOutcomePublisherTest {

    @Mock
    private KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;

    @InjectMocks
    private KafkaEventOutcomePublisher kafkaEventOutcomePublisher;

    @Test
    void sendsEventOutcomeMessageToConfiguredTopic() {
        EventOutcomeMessage message = new EventOutcomeMessage("event-1", "Match A", "winner-1");

        kafkaEventOutcomePublisher.publish(message);

        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(message));
    }
}
```

Update `PublishEventOutcomeServiceTest.java` to also verify that the publisher is invoked.

**Step 2: Run the tests to verify they fail**

Run: `./mvnw -q -pl event-outcome-service -Dtest=KafkaEventOutcomePublisherTest,PublishEventOutcomeServiceTest test`
Expected: FAIL because the message mapper and Kafka publisher do not exist yet.

**Step 3: Write the minimal implementation**

`EventOutcomeMessageMapper.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.mappers;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import com.sporty.groupha.eventoutcome.domain.EventOutcome;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventOutcomeMessageMapper {

    EventOutcomeMessage toMessage(EventOutcome eventOutcome);
}
```

`KafkaEventOutcomePublisher.java`:

```java
package com.sporty.groupha.eventoutcome.infrastructure.messaging;

import com.sporty.groupha.commonlib.messaging.event.EventOutcomeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventOutcomePublisher {

    private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;
    private final String topicName;

    public KafkaEventOutcomePublisher(
            KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate,
            @Value("${app.messaging.event-outcomes-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publish(EventOutcomeMessage eventOutcomeMessage) {
        kafkaTemplate.send(topicName, eventOutcomeMessage);
    }
}
```

Modify the service so it saves first, maps to `EventOutcomeMessage`, then publishes.

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl event-outcome-service -Dtest=KafkaEventOutcomePublisherTest,PublishEventOutcomeServiceTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add event-outcome-service
git commit -m "feat: publish accepted event outcomes to kafka"
```

### Task 6: Implement Bet Persistence, Seed Data, and Settlement Domain Logic

**Files:**
- Create: `bet-matching-service/src/main/resources/db/migration/V1__create_bets.sql`
- Create: `bet-matching-service/src/main/resources/db/migration/V2__seed_bets.sql`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/domain/SettlementResult.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/domain/EventOutcome.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/domain/SettlementDecision.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/domain/Bet.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/entity/BetEntity.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/persistence/BetJpaRepository.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetEntityMapper.java`
- Create: `bet-matching-service/src/main/resources/application.yml`
- Test: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/domain/BetTest.java`
- Test: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/persistence/BetJpaRepositoryTest.java`

**Step 1: Write the failing tests**

`BetTest.java`:

```java
package com.sporty.groupha.betmatching.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BetTest {

    @Test
    void returnsWonWhenBetWinnerMatchesActualWinner() {
        Bet bet = new Bet("bet-1", "user-1", "event-1", "market-1", "winner-1", new BigDecimal("10.00"));
        EventOutcome eventOutcome = new EventOutcome("event-1", "Match A", "winner-1");

        SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);

        assertThat(settlementDecision.result()).isEqualTo(SettlementResult.WON);
    }
}
```

`BetJpaRepositoryTest.java`:

```java
package com.sporty.groupha.betmatching.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BetJpaRepositoryTest {

    @Autowired
    private BetJpaRepository betJpaRepository;

    @Test
    void findsBetsByEventId() {
        assertThat(betJpaRepository.findByEventId("event-1")).isNotEmpty();
    }
}
```

**Step 2: Run the tests to verify they fail**

Run: `./mvnw -q -pl bet-matching-service -Dtest=BetTest,BetJpaRepositoryTest test`
Expected: FAIL because the domain model, migrations, and repository are missing.

**Step 3: Write the minimal implementation**

`SettlementResult.java`:

```java
package com.sporty.groupha.betmatching.domain;

public enum SettlementResult {
    WON,
    LOST
}
```

`Bet.java`:

```java
package com.sporty.groupha.betmatching.domain;

import java.math.BigDecimal;

public record Bet(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String eventWinnerId,
        BigDecimal betAmount
) {

    public SettlementDecision settleAgainst(EventOutcome eventOutcome) {
        SettlementResult settlementResult = eventWinnerId.equals(eventOutcome.eventWinnerId())
                ? SettlementResult.WON
                : SettlementResult.LOST;

        return new SettlementDecision(
                betId,
                userId,
                eventId,
                eventMarketId,
                eventWinnerId,
                eventOutcome.eventWinnerId(),
                betAmount,
                settlementResult
        );
    }
}
```

`V1__create_bets.sql` should create the `bets` table with all required columns and `result` omitted because the settlement service owns final settlement persistence.

`V2__seed_bets.sql` should insert a small set of bets for at least two events, with multiple outcomes for one event to prove `WON` and `LOST` flows.

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl bet-matching-service -Dtest=BetTest,BetJpaRepositoryTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add bet-matching-service
git commit -m "feat: add bet persistence and settlement domain logic"
```

### Task 7: Consume Kafka Outcomes and Publish RocketMQ Settlement Messages

**Files:**
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeService.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/EventOutcomeMessageMapper.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/mappers/BetSettlementMessageMapper.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/messaging/EventOutcomeKafkaListener.java`
- Create: `bet-matching-service/src/main/java/com/sporty/groupha/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisher.java`
- Modify: `bet-matching-service/src/main/resources/application.yml`
- Test: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/services/ProcessEventOutcomeServiceTest.java`
- Test: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/messaging/EventOutcomeKafkaListenerTest.java`
- Test: `bet-matching-service/src/test/java/com/sporty/groupha/betmatching/infrastructure/messaging/RocketMqBetSettlementPublisherTest.java`

**Step 1: Write the failing tests**

`ProcessEventOutcomeServiceTest.java`:

```java
package com.sporty.groupha.betmatching.services;

import com.sporty.groupha.betmatching.infrastructure.persistence.BetJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessEventOutcomeServiceTest {

    @Mock
    private BetJpaRepository betJpaRepository;

    @Mock
    private com.sporty.groupha.betmatching.infrastructure.messaging.RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher;

    @InjectMocks
    private ProcessEventOutcomeService processEventOutcomeService;

    @Test
    void publishesOneSettlementPerMatchedBet() {
        processEventOutcomeService.process("event-1", "Match A", "winner-1");

        verify(betJpaRepository).findByEventId("event-1");
    }
}
```

`EventOutcomeKafkaListenerTest.java` should verify that the listener delegates to the service.

`RocketMqBetSettlementPublisherTest.java` should verify that `RocketMQTemplate` is called with the configured topic and message.

**Step 2: Run the tests to verify they fail**

Run: `./mvnw -q -pl bet-matching-service -Dtest=ProcessEventOutcomeServiceTest,EventOutcomeKafkaListenerTest,RocketMqBetSettlementPublisherTest test`
Expected: FAIL because the service, mappers, listener, and publisher are missing.

**Step 3: Write the minimal implementation**

`ProcessEventOutcomeService.java`:

```java
package com.sporty.groupha.betmatching.services;

import com.sporty.groupha.betmatching.domain.Bet;
import com.sporty.groupha.betmatching.domain.EventOutcome;
import com.sporty.groupha.betmatching.domain.SettlementDecision;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetEntityMapper;
import com.sporty.groupha.betmatching.infrastructure.mappers.BetSettlementMessageMapper;
import com.sporty.groupha.betmatching.infrastructure.persistence.BetJpaRepository;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessEventOutcomeService {

    private final BetJpaRepository betJpaRepository;
    private final BetEntityMapper betEntityMapper;
    private final BetSettlementMessageMapper betSettlementMessageMapper;
    private final com.sporty.groupha.betmatching.infrastructure.messaging.RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher;

    public ProcessEventOutcomeService(
            BetJpaRepository betJpaRepository,
            BetEntityMapper betEntityMapper,
            BetSettlementMessageMapper betSettlementMessageMapper,
            com.sporty.groupha.betmatching.infrastructure.messaging.RocketMqBetSettlementPublisher rocketMqBetSettlementPublisher
    ) {
        this.betJpaRepository = betJpaRepository;
        this.betEntityMapper = betEntityMapper;
        this.betSettlementMessageMapper = betSettlementMessageMapper;
        this.rocketMqBetSettlementPublisher = rocketMqBetSettlementPublisher;
    }

    public void process(String eventId, String eventName, String eventWinnerId) {
        EventOutcome eventOutcome = new EventOutcome(eventId, eventName, eventWinnerId);
        List<Bet> bets = betJpaRepository.findByEventId(eventId)
                .stream()
                .map(betEntityMapper::toDomain)
                .toList();

        for (Bet bet : bets) {
            SettlementDecision settlementDecision = bet.settleAgainst(eventOutcome);
            BetSettlementMessage settlementMessage = betSettlementMessageMapper.toMessage(settlementDecision);
            rocketMqBetSettlementPublisher.publish(settlementMessage);
        }
    }
}
```

`EventOutcomeKafkaListener.java` should use `@KafkaListener` with the `event-outcomes` topic property and delegate immediately to the service.

`RocketMqBetSettlementPublisher.java` should call `RocketMQTemplate.syncSend(...)` using the configured `bet-settlements` topic.

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl bet-matching-service -Dtest=ProcessEventOutcomeServiceTest,EventOutcomeKafkaListenerTest,RocketMqBetSettlementPublisherTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add bet-matching-service
git commit -m "feat: process kafka outcomes and publish rocketmq settlements"
```

### Task 8: Consume RocketMQ Messages and Persist Settlements

**Files:**
- Create: `bet-settlement-service/src/main/resources/db/migration/V1__create_bet_settlements.sql`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/domain/SettlementResult.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/domain/BetSettlement.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementService.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/entity/BetSettlementEntity.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/persistence/BetSettlementJpaRepository.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementEntityMapper.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/mappers/BetSettlementMessageMapper.java`
- Create: `bet-settlement-service/src/main/java/com/sporty/groupha/betsettlement/infrastructure/messaging/BetSettlementRocketMqListener.java`
- Create: `bet-settlement-service/src/main/resources/application.yml`
- Test: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/domain/BetSettlementTest.java`
- Test: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/services/ApplyBetSettlementServiceTest.java`
- Test: `bet-settlement-service/src/test/java/com/sporty/groupha/betsettlement/infrastructure/messaging/BetSettlementRocketMqListenerTest.java`

**Step 1: Write the failing tests**

`BetSettlementTest.java`:

```java
package com.sporty.groupha.betsettlement.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetSettlementTest {

    @Test
    void rejectsBlankBetId() {
        assertThatThrownBy(() -> BetSettlement.create(" ", "user-1", "event-1", "market-1", "winner-1", "winner-1", new BigDecimal("10.00"), SettlementResult.WON))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("betId");
    }
}
```

`ApplyBetSettlementServiceTest.java` should verify that the settlement entity is saved.

`BetSettlementRocketMqListenerTest.java` should verify delegation to the service.

**Step 2: Run the tests to verify they fail**

Run: `./mvnw -q -pl bet-settlement-service -Dtest=BetSettlementTest,ApplyBetSettlementServiceTest,BetSettlementRocketMqListenerTest test`
Expected: FAIL because the domain model, listener, entity, repository, and service are missing.

**Step 3: Write the minimal implementation**

`BetSettlement.java`:

```java
package com.sporty.groupha.betsettlement.domain;

import java.math.BigDecimal;

public record BetSettlement(
        String betId,
        String userId,
        String eventId,
        String eventMarketId,
        String expectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        SettlementResult result
) {

    public static BetSettlement create(
            String betId,
            String userId,
            String eventId,
            String eventMarketId,
            String expectedWinnerId,
            String actualWinnerId,
            BigDecimal betAmount,
            SettlementResult result
    ) {
        validate("betId", betId);
        validate("userId", userId);
        validate("eventId", eventId);
        validate("eventMarketId", eventMarketId);
        return new BetSettlement(betId.trim(), userId.trim(), eventId.trim(), eventMarketId.trim(), expectedWinnerId.trim(), actualWinnerId.trim(), betAmount, result);
    }

    private static void validate(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
```

`BetSettlementRocketMqListener.java` should use `@RocketMQMessageListener` on the `bet-settlements` topic and immediately delegate to `ApplyBetSettlementService`.

**Step 4: Run the tests to verify they pass**

Run: `./mvnw -q -pl bet-settlement-service -Dtest=BetSettlementTest,ApplyBetSettlementServiceTest,BetSettlementRocketMqListenerTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add bet-settlement-service
git commit -m "feat: consume rocketmq settlements and persist them"
```

### Task 9: Configure Runtime Properties, Dockerfiles, and Docker Compose

**Files:**
- Modify: `event-outcome-service/src/main/resources/application.yml`
- Modify: `bet-matching-service/src/main/resources/application.yml`
- Modify: `bet-settlement-service/src/main/resources/application.yml`
- Create: `event-outcome-service/Dockerfile`
- Create: `bet-matching-service/Dockerfile`
- Create: `bet-settlement-service/Dockerfile`
- Create: `docker-compose.yml`

**Step 1: Write the failing runtime check**

Run: `docker compose config`
Expected: FAIL because `docker-compose.yml` does not exist yet.

**Step 2: Write the minimal runtime configuration**

Each `application.yml` should include:
- service port,
- H2 datasource,
- Flyway enabled,
- Kafka or RocketMQ connection settings,
- topic names under `app.messaging`.

Example for `event-outcome-service`:

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:h2:mem:eventoutcomedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
  kafka:
    bootstrap-servers: kafka:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

app:
  messaging:
    event-outcomes-topic: event-outcomes
```

`docker-compose.yml` should define:
- a Kafka container using the latest usable Docker image tag,
- a RocketMQ nameserver container,
- a RocketMQ broker container,
- `event-outcome-service`,
- `bet-matching-service`,
- `bet-settlement-service`.

If a plain `latest` tag is unavailable for a required image, replace it with the current latest stable tag and record the exact tag in the README.

**Step 3: Add Dockerfiles**

Use a simple JAR-based Dockerfile per service:

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Step 4: Run the runtime check again**

Run: `docker compose config`
Expected: PASS.

**Step 5: Commit**

```bash
git add docker-compose.yml event-outcome-service/Dockerfile bet-matching-service/Dockerfile bet-settlement-service/Dockerfile event-outcome-service/src/main/resources/application.yml bet-matching-service/src/main/resources/application.yml bet-settlement-service/src/main/resources/application.yml
git commit -m "feat: add docker compose runtime for kafka rocketmq and services"
```

### Task 10: Write a High-Quality README for the Assignment

**Files:**
- Create: `README.md`

**Step 1: Write the failing documentation check**

Run: `rg -n "^## (Architecture|Module Responsibilities|How to Run|API Usage|Testing Strategy|Trade-offs)$" README.md`
Expected: FAIL because `README.md` does not exist yet.

**Step 2: Write the README**

The README must include these sections and actual content for each:
- assignment purpose,
- architecture,
- module responsibilities,
- design choices,
- exact Java / Spring Boot / MapStruct / RocketMQ Spring versions,
- exact Docker image tags used in Compose,
- local run instructions,
- API usage with `curl` example,
- end-to-end message flow,
- test strategy,
- deliberate simplifications and next improvements.

Include an example request:

```bash
curl -X POST http://localhost:8081/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "event-1",
    "eventName": "Arsenal vs Benfica",
    "eventWinnerId": "winner-1"
  }'
```

**Step 3: Run the documentation check again**

Run: `rg -n "^## (Architecture|Module Responsibilities|How to Run|API Usage|Testing Strategy|Trade-offs)$" README.md`
Expected: PASS.

**Step 4: Commit**

```bash
git add README.md
git commit -m "docs: add assignment readme"
```

### Task 11: Final Verification and Review Request

**Files:**
- Modify if needed: any failing module from prior tasks

**Step 1: Run the full verification suite**

Run: `./mvnw test`
Expected: PASS for all modules.

**Step 2: Verify Docker Compose configuration**

Run: `docker compose config`
Expected: PASS.

**Step 3: Smoke-check the built artifacts**

Run: `./mvnw -q clean package -DskipTests`
Expected: PASS and generate service jars.

**Step 4: Review git status**

Run: `git status --short`
Expected: clean working tree.

**Step 5: Request review**

Use `@requesting-code-review` before merging or sharing the assignment.
