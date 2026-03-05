# AGENTS.md

## Purpose
This file defines how coding agents should work in this repository.

## Project Context
- The system processes sports event outcomes and triggers bet settlement through a event-driven workflow.
- Multi-module Maven repository with three services: `event-outcome-service`, `bet-matching-service`, and `bet-settlement-service`.
- Shared message contracts live in `common-lib/`.
- Core workflow is event-driven (Kafka + RocketMQ) with REST entry points.

## Working Rules
- Preserve module and service boundaries; avoid cross-service coupling.
- Update or add tests whenever behavior changes.
- Prefer explicit, readable code over clever shortcuts.
- When uncertain about library behavior, APIs, or solution design, consult Context7 MCP for authoritative library context before implementing.

## Implementation Patterns
- Use MapStruct as the default mapping approach anywhere object translation is needed, across all layers and boundaries.
- Apply Domain-Driven Design with rich domain models: business rules belong in domain objects, not in controllers or repositories.
- Follow folder structure: domain, services, and infrastructure (mappers, persistence, entity, controller, dto, messaging).
- Enforce inward dependencies only: outer layers can depend on inner layers, never the opposite.
- No need for Port and adapters interfaces right now - don't do over engineering 
- Keep framework, transport, and persistence concerns out of domain models.
- Perform transformations at boundaries (API DTOs, message contracts, persistence entities), not inside core business logic.
- Prefer immutable request/response/message models when practical.
- Extract values into named variables before invocation for readability/debuggability.
- No method call inline in params.
- Always use enums over strings where applicable.
- Persist enums safely in JPA with @Enumerated(EnumType.STRING) to keep readable/stable storage.
