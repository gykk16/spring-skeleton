# Module Dependencies

## Overview

This project implements a pragmatic hexagonal architecture combining Layered Architecture with Ports & Adapters patterns. Dependencies flow unidirectionally from outer layers (bootstrap/runtime) toward inner layers (foundation/domain), with automatic dependency injection configured at the root build script level.

## Module Inventory

| Module | Type | Package Root | Purpose |
|--------|------|--------------|---------|
| `common` | Foundation | `io.glory.common` | Core utilities, value objects (Email, Money, Rate), exceptions, date/time handling |
| `common-web` | Web Infrastructure | `io.glory.commonweb` | Filters, interceptors, exception handlers, ApiResource response format |
| `test-support` | Test Fixtures | - | Shared test fixtures library (`java-test-fixtures` plugin) |
| `infrastructure` | Technical | `io.glory.infrastructure` | Cache (Caffeine, Redisson), DB (JPA, QueryDSL), HTTP clients, POI, Slack |
| `todo-application` | Application | `io.glory.todoapplication` | Reference application: TodoClient, TodoService |
| `skeleton-api-app` | Bootstrap (app) | `io.glory.skeletonapiapp` | REST API server (Spring Boot executable) |
| `skeleton-worker-app` | Bootstrap (app) | `io.glory.skeletonworkerapp` | Worker/scheduler server (Spring Boot executable) |
| `domain` _(planned)_ | Domain | `io.glory.domain` | Pure business rules and domain models |

## Architecture Diagrams

### Complete Module Dependency Graph

```mermaid
graph TB
    subgraph Bootstrap["Bootstrap Layer"]
        direction LR
        API["skeleton-api-app<br/><small>REST API Server</small>"]
        Worker["skeleton-worker-app<br/><small>Worker Server</small>"]
    end

    subgraph Application["Application Layer"]
        TodoApp["todo-application<br/><small>TodoClient, TodoService</small>"]
    end

    subgraph Infra["Infrastructure Layer"]
        Infrastructure["infrastructure<br/><small>Cache, DB, HTTP, Export</small>"]
    end

    subgraph WebInfra["Web Infrastructure"]
        CommonWeb["common-web<br/><small>Filters, Handlers, ApiResource</small>"]
    end

    subgraph Foundation["Foundation"]
        Common["common<br/><small>Values, Utils, Exceptions</small>"]
    end

    subgraph TestLayer["Test Support"]
        TestSupport["test-support<br/><small>Test Fixtures</small>"]
    end

    subgraph Planned["Planned"]
        Domain["domain<br/><small>Business Rules</small>"]
    end

    %% Explicit dependencies (solid)
    API -->|impl| TodoApp
    TodoApp -->|api| Infrastructure

    %% Auto-injected by root build (dashed)
    API -.->|auto| CommonWeb
    API -.->|auto| Infrastructure
    Worker -.->|auto| CommonWeb
    Worker -.->|auto| Infrastructure
    TodoApp -.->|auto| Common
    Infrastructure -.->|auto| Common
    CommonWeb -.->|auto| Common

    %% Test dependencies (dotted)
    API -.->|test| TestSupport
    Worker -.->|test| TestSupport
    TestSupport -->|testFixturesApi| Common
    TestSupport -->|testFixturesApi| CommonWeb

    %% Planned future dependencies
    TodoApp -.->|planned| Domain
    Domain -.->|planned| Common

    %% Styling
    style Bootstrap fill:#ffebee,color:#333
    style Application fill:#e8f5e9,color:#333
    style Infra fill:#fff3e0,color:#333
    style WebInfra fill:#f3e5f5,color:#333
    style Foundation fill:#e1f5fe,color:#333
    style TestLayer fill:#fff9c4,stroke:#ccc,stroke-dasharray:5,color:#333
    style Planned fill:#fafafa,stroke:#ccc,stroke-dasharray:5,color:#666
```

### Layered Architecture View

```mermaid
graph TD
    subgraph Layer6["Layer 6: Runtime Entrypoints"]
        direction LR
        API["skeleton-api-app<br/><small>Controllers + Main</small>"]
        Worker["skeleton-worker-app<br/><small>Schedulers + Main</small>"]
    end

    subgraph Layer5["Layer 5: Application Services"]
        TodoApp["todo-application<br/><small>Use Cases, Orchestration</small>"]
    end

    subgraph Layer4["Layer 4: Domain Models - Planned"]
        Domain["domain<br/><small>Business Rules</small>"]
    end

    subgraph Layer3["Layer 3: Technical Infrastructure"]
        Infra["infrastructure<br/><small>DB, Cache, HTTP, Export</small>"]
    end

    subgraph Layer2["Layer 2: Web Infrastructure"]
        CommonWeb["common-web<br/><small>Filters, Handlers, ApiResource</small>"]
    end

    subgraph Layer1["Layer 1: Foundation"]
        Common["common<br/><small>Utilities, Values, Exceptions</small>"]
    end

    API --> TodoApp
    API -.-> CommonWeb
    API -.-> Infra
    Worker -.-> CommonWeb
    Worker -.-> Infra

    TodoApp --> Infra
    TodoApp -.->|planned| Domain

    Domain -.->|planned| Common

    Infra --> Common
    CommonWeb --> Common

    style Layer6 fill:#ffebee,color:#333
    style Layer5 fill:#e8f5e9,color:#333
    style Layer4 fill:#fafafa,stroke:#ccc,stroke-dasharray:5,color:#666
    style Layer3 fill:#fff3e0,color:#333
    style Layer2 fill:#f3e5f5,color:#333
    style Layer1 fill:#e1f5fe,color:#333
```

### Dependency Injection Flow

```mermaid
flowchart LR
    Root["root build.gradle.kts"]

    subgraph AllScope["All Subprojects"]
        AllBundles["kotlin bundle<br/>jackson bundle<br/>test bundle<br/>Kotlin/Spring/JPA plugins<br/>Spring Boot BOM"]
    end

    subgraph NonCommonScope["Non-Common Modules"]
        NonCommonDeps["common module<br/>lombok"]
    end

    subgraph AppScope["-app Modules"]
        AppsDeps["common-web<br/>infrastructure<br/>Spring Boot plugin<br/>KAPT plugin<br/>bootJar enabled"]
    end

    Root -->|"configure all"| AllScope
    Root -->|"if name != common"| NonCommonScope
    Root -->|"if name endsWith -app"| AppScope

    style Root fill:#e3f2fd,color:#333
    style AllScope fill:#f3e5f5,color:#333
    style NonCommonScope fill:#fff3e0,color:#333
    style AppScope fill:#ffebee,color:#333
```

## Dependency Rules

### Rule 1: Zero External Dependencies for Foundation
**Evidence**: `common` module has zero project dependencies, only external libraries (`libphonenumber`).

### Rule 2: Unidirectional Dependency Flow
**Evidence**: Dependencies flow from runtime (`*-app`) → application → infrastructure → foundation. No reverse dependencies exist.

### Rule 3: Auto-Injection for Common Modules
**Evidence**: Root build script automatically injects `common` to all non-common modules, `common-web` + `infrastructure` to all `-app` modules.

### Rule 4: Test-Only Dependencies Isolated
**Evidence**: `test-support` module uses `testFixturesApi` scope, consumed only via `testImplementation(testFixtures())`.

### Rule 5: Bootstrap Modules Are Leaves
**Evidence**: No module depends on `*-app` modules; they are terminal runtime nodes.

### Rule 6: Domain Independence (Planned)
**Evidence**: Future `domain` module will depend only on `common`, ensuring domain isolation.

## Auto-Injection Summary

| Target Scope | Injected Dependencies | Applied To |
|--------------|----------------------|------------|
| All subprojects | `kotlin`, `jackson`, `test` bundles | Every module |
| Non-common modules | `common` module + lombok | All except `common` |
| `-app` modules | `common-web` + `infrastructure` + Spring Boot + KAPT | `skeleton-api-app`, `skeleton-worker-app` |

## Quick Reference

### Dependency Direction (Top → Bottom)

```
skeleton-api-app / skeleton-worker-app    ← Bootstrap (executables)
    ├── todo-application                  ← Application services
    │       └── infrastructure            ← Technical implementations
    │               └── common            ← Foundation
    ├── common-web ──────────┘            ← Web infrastructure
    │       └── common
    └── test-support (test only)          ← Test fixtures
            ├── common
            └── common-web
```

### Module Import Guidelines

- **common**: Import for value objects (Email, Money, Rate), exceptions, utilities
- **common-web**: Import for ApiResource, filters, interceptors, exception handlers
- **infrastructure**: Import for cache, DB, HTTP clients, technical integrations
- **test-support**: Import via `testImplementation(testFixtures())` for test fixtures
- **Bootstrap apps**: Never import these in other modules

### Test Dependencies

```
*-app modules → testFixtures(test-support) → common + common-web
```
