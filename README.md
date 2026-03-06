# Sports Betting Settlement Workflow

## Purpose
This repository implements an event-driven sports betting settlement workflow for a hiring assignment.

The system accepts an event outcome over HTTP, publishes it to Kafka, matches affected bets, publishes settlement commands to RocketMQ, and persists the final settlement result.

## Architecture
The codebase is a Maven mono-repo with four modules:

- `common-lib`
- `event-outcome-service`
- `bet-matching-service`
- `bet-settlement-service`

The implementation follows a pragmatic DDD and clean-architecture direction:

- `domain`: rich domain models and business rules
- `services`: orchestration only
- `infrastructure`: transport, persistence, messaging, DTOs, mappers

Boundary rules used in this solution:

- controllers handle HTTP only
- services orchestrate use cases
- repositories and publishers/listeners contain no business logic
- MapStruct is used at object translation boundaries
- domain models stay free from Spring, JPA, and transport concerns

End-to-end flow:

```text
event-outcome-service (HTTP)
  -> Kafka topic: event-outcomes
  -> bet-matching-service (consumer)
  -> RocketMQ topic: bet-settlements
  -> bet-settlement-service (consumer)
  -> H2 persistence of final settlement result
```

## Module Responsibilities
`common-lib`
- Holds shared immutable message contracts only.

`event-outcome-service`
- Exposes `POST /api/event-outcomes`.
- Validates the HTTP payload.
- Persists accepted outcomes in its own H2 database.
- Publishes `EventOutcomeMessage` to Kafka.

`bet-matching-service`
- Consumes `EventOutcomeMessage` from Kafka.
- Loads seeded bets from its own H2 database.
- Uses domain logic to compute `WON` or `LOST`.
- Publishes `BetSettlementMessage` to RocketMQ.

`bet-settlement-service`
- Consumes `BetSettlementMessage` from RocketMQ.
- Validates and maps the message into a rich domain model.
- Persists the final settlement record in its own H2 database.

## Versions
Application and library versions used in the implementation:

- Java: `17`
- Spring Boot: `3.5.11`
- MapStruct: `1.6.3`
- RocketMQ Spring Boot Starter: `2.3.4`

Messaging and runtime image tags used in `docker-compose.yml`:

- Apache Kafka: `apache/kafka:4.2.0`
- Apache RocketMQ: `apache/rocketmq:5.4.0`
- Java runtime base image for service containers: `eclipse-temurin:17-jre`

## Demo Data
`bet-matching-service` seeds these bets through Flyway:

- `bet-1` for `event-1`, predicted winner `winner-1`, amount `10.00`
- `bet-2` for `event-1`, predicted winner `winner-2`, amount `20.00`
- `bet-3` for `event-2`, predicted winner `winner-3`, amount `15.00`

If you publish an outcome for `event-1` with `eventWinnerId=winner-1`:

- `bet-1` becomes `WON`
- `bet-2` becomes `LOST`
- `bet-3` is unaffected

## How to Run
Prerequisites:

- Docker with the Compose plugin
- Java `17`

Build the JARs first because each Dockerfile copies `target/*.jar`:

```bash
./mvnw -q clean package
```

Start the stack:

```bash
docker compose up --build
```

Exposed entry points:

- `event-outcome-service`: `http://localhost:8081`
- Kafka external listener for local debugging: `localhost:9092`
- RocketMQ nameserver: `localhost:9876`

Notes:

- `bet-matching-service` and `bet-settlement-service` are worker services. They do not expose public HTTP endpoints.
- All service databases are in-memory H2 instances, so data resets whenever the containers stop.

## API Usage
Publish an event outcome:

```bash
curl -X POST http://localhost:8081/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "event-1",
    "eventName": "Arsenal vs Benfica",
    "eventWinnerId": "winner-1"
  }'
```

Expected response:

```text
HTTP/1.1 202 Accepted
```

That single request should trigger:

- one event outcome persisted by `event-outcome-service`
- two matched bets processed by `bet-matching-service`
- two settlement rows persisted by `bet-settlement-service`

## Database Access
Each service has its own H2 database instance, configured in `application.properties`. You can connect to them using the H2 Console or any JDBC client:
 
| Service | Console URL | JDBC URL |
| --- | --- | --- |
| Event Outcome | http://localhost:8081/h2-console | `jdbc:h2:mem:eventoutcomedb` |
| Bet Matching | http://localhost:8082/h2-console | `jdbc:h2:mem:betmatchingdb` |
| Bet Settlement | http://localhost:8083/h2-console | `jdbc:h2:mem:betsettlementdb` |

## Testing Strategy
The current test suite is intentionally fast and focused:

- domain unit tests for business rules and invariants
- service unit tests for orchestration
- listener and publisher unit tests with Mockito
- @WebMvcTest for the HTTP controller
- @DataJpaTest for repository query behavior where it adds value

Verification commands used during implementation:

```bash
./mvnw test
```

Intentionally excluded in this phase:

- integration tests
- Testcontainers
- embedded Kafka or RocketMQ broker tests
- end-to-end automated Compose smoke tests

## Trade-offs
Deliberate simplifications for the assignment:

- no outbox pattern
- no retry orchestration or dead-letter flow
- no idempotency safeguards on message consumption
- no distributed tracing
- no authentication or authorization
- no production-grade observability setup
- H2 is in-memory per service, so persistence is ephemeral
- Dockerfiles depend on a prior Maven package step instead of doing a multi-stage build

These trade-offs keep the code readable and focused on the core event-driven workflow while still demonstrating:

- clearly defined service boundaries
- rich, business-focused domain models
- a pragmatic Clean Architecture structure
- strong object-oriented design
- clear ownership of persistence within each service
- a lean, practical microservice setup
- consistent application of SOLID principles
- a maintainable foundation that can evolve toward more production-ready capabilities
