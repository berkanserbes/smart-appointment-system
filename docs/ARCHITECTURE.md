# Architecture

## Overview

Smart Appointment System is a layered Spring Boot REST API. Each layer has a single, well-defined responsibility and communicates only with the layer directly below it.

## Layers

```
┌──────────────────────────────────┐
│         Controller Layer         │  HTTP in / HTTP out
├──────────────────────────────────┤
│          Service Layer           │  Business logic & transactions
├──────────────────────────────────┤
│        Repository Layer          │  Data access (JPA)
├──────────────────────────────────┤
│         PostgreSQL / Redis       │  Persistence & cache
└──────────────────────────────────┘
```

| Layer | Responsibility |
|---|---|
| **Controller** | Validates requests (`@Valid`), delegates to service, returns HTTP responses |
| **Service** | Business rules, conflict checks, authorization, transaction boundaries |
| **Repository** | Spring Data JPA queries, no business logic |

---

## Design Patterns

### Dependency Inversion
Services are consumed through interfaces (`IAppointmentService`, `ISender`, etc.). Concrete implementations live in `service/impl`. This makes the codebase testable and swappable without touching consumers.

```
Controller → IAppointmentService ← AppointmentService (impl)
ReminderService → ISender ← EmailSenderService (impl)
```

### Repository Pattern
Data access is fully abstracted behind Spring Data JPA repositories. Services never write raw queries.

### DTO Pattern
API contracts (`CreateAppointmentRequest`, `AppointmentResponse`) are kept strictly separate from JPA entities. This prevents internal model details leaking into the API.

```
Request DTO → Service → Entity → Repository
Repository → Entity → Mapper → Response DTO
```

### Mapper Pattern
Dedicated mapper classes (`AppointmentMapper`, `UserMapper`, …) handle all entity↔DTO conversions. No mapping logic in controllers or services.

### Builder Pattern
Entities are constructed with Lombok `@Builder`. This keeps creation readable and avoids telescoping constructors.

---

## Security

### Authentication & Authorization Flow

```
Request
  └── JwtAuthenticationFilter
        ├── Valid token → populate SecurityContext → proceed
        └── Invalid / missing → 401 Unauthorized

Controller
  └── @PreAuthorize("hasRole('ADMIN')") → 403 if role mismatch
```

JWT tokens are stateless (the server stores no session). Token payload carries `sub` (email) and `role`.

---

## Cross-Cutting Concerns

### Caching (Redis)
Frequently read data (categories, providers, users, reports) is cached with `@Cacheable`. Write operations evict stale entries with `@CacheEvict`. If Redis is unavailable, `CacheErrorHandler` catches the error, logs a warning, and falls back to the database.

### Email Notifications
`EmailSenderService` implements `ISender` and uses Spring's `JavaMailSender` over SMTP. `ReminderService` depends on `ISender` (not the concrete class), satisfying DIP. A `@Scheduled` task runs every 60 seconds and dispatches pending reminders.

### Exception Handling
`GlobalExceptionHandler` (`@ControllerAdvice`) catches all domain exceptions and maps them to consistent JSON error responses.

| Exception | HTTP Status |
|---|---|
| `BadRequestException` | 400 |
| `UnauthorizedException` | 401 |
| `ForbiddenException` | 403 |
| `ResourceNotFoundException` | 404 |
| `ConflictException` | 409 |

### Audit Logging
Sensitive operations (login, appointment create/cancel, user changes) are recorded in the `audit_logs` table with user, action, entity, timestamp, IP address.

---

## Package Structure

```
com.smartappointment
├── config          # SecurityConfig, RedisConfig, OpenApiConfig, DataInitializer
├── controller      # REST endpoints
├── service
│   ├── interfaces  # IAppointmentService, ISender, …
│   └── impl        # AppointmentService, EmailSenderService, …
├── repository      # Spring Data JPA repositories
├── dto
│   ├── */requests  # Incoming payloads
│   └── */responses # Outgoing responses
├── model
│   ├── entity      # JPA entities
│   └── enums       # Status, Role, ReminderType, …
├── mapper          # Entity ↔ DTO converters
├── security        # JwtUtil, JwtAuthenticationFilter
└── exception       # Custom exceptions, GlobalExceptionHandler
```

