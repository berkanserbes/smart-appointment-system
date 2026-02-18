# Database Schema

## Overview

PostgreSQL database with 8 main tables for appointment management system.

## Entity Relationship Diagram

```
users (1) ──────< (N) appointments (N) ────── (1) service_providers
  │                      │                              │
  │                      │                              │
  └──< (N) feedbacks     ├──< (N) reminders           (N)
                         │                              │
                         └──── (1) locations           (1)
                         │                              │
                         └──── (1) feedback        categories
                         
audit_logs (N) ────── (1) users
```

## Tables

### users
User accounts with role-based access.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| first_name | VARCHAR(50) | NOT NULL | User first name |
| last_name | VARCHAR(50) | NOT NULL | User last name |
| email | VARCHAR(100) | NOT NULL, UNIQUE | User email |
| password | VARCHAR(255) | NOT NULL | Encrypted password |
| phone | VARCHAR(20) | | Phone number |
| role | VARCHAR(20) | NOT NULL | USER or ADMIN |
| active | BOOLEAN | NOT NULL, DEFAULT true | Account status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### categories
Appointment categories for grouping services.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Category name |
| description | VARCHAR(500) | | Category description |
| color_code | VARCHAR(7) | DEFAULT '#3B82F6' | UI color code |
| default_duration_minutes | INT | NOT NULL, DEFAULT 30 | Default appointment duration |
| active | BOOLEAN | NOT NULL, DEFAULT true | Category status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### locations
Physical locations for appointments.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL | Location name |
| address | VARCHAR(255) | | Full address |
| city | VARCHAR(50) | | City name |
| active | BOOLEAN | NOT NULL, DEFAULT true | Location status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### service_providers
Professionals providing services.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL | Provider name |
| title | VARCHAR(50) | | Professional title |
| phone | VARCHAR(20) | | Contact phone |
| email | VARCHAR(100) | | Contact email |
| description | VARCHAR(1000) | | Provider description |
| category_id | BIGINT | NOT NULL, FK | Category reference |
| active | BOOLEAN | NOT NULL, DEFAULT true | Provider status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

**Foreign Keys:**
- category_id → categories(id)

### appointments
Scheduled appointments between users and providers.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | User reference |
| provider_id | BIGINT | NOT NULL, FK | Provider reference |
| location_id | BIGINT | NOT NULL, FK | Location reference |
| start_time | TIMESTAMP | NOT NULL | Appointment start |
| end_time | TIMESTAMP | NOT NULL | Appointment end |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'SCHEDULED' | Appointment status |
| cancellation_reason | VARCHAR(500) | | Reason for cancellation |
| cancelled_at | TIMESTAMP | | Cancellation timestamp |
| cancelled_by | VARCHAR(20) | | Who cancelled |
| confirmed_at | TIMESTAMP | | Confirmation timestamp |
| notes | VARCHAR(1000) | | Additional notes |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

**Foreign Keys:**
- user_id → users(id)
- provider_id → service_providers(id)
- location_id → locations(id)

**Indexes:**
- idx_appointment_user (user_id)
- idx_appointment_provider (provider_id)
- idx_appointment_start_time (start_time)
- idx_appointment_status (status)

**Status Values:**
- SCHEDULED
- CONFIRMED
- CANCELLED
- COMPLETED
- MISSED
- REJECTED

### feedbacks
User feedback for completed appointments.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| appointment_id | BIGINT | NOT NULL, UNIQUE, FK | Appointment reference |
| user_id | BIGINT | NOT NULL, FK | User reference |
| rating | INT | NOT NULL | Rating (1-5) |
| comment | VARCHAR(1000) | | Feedback comment |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

**Foreign Keys:**
- appointment_id → appointments(id)
- user_id → users(id)

**Indexes:**
- idx_feedback_user (user_id)
- idx_feedback_appointment (appointment_id)
- idx_feedback_rating (rating)
- idx_feedback_created (created_at)

### reminders
Notification reminders for appointments.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| appointment_id | BIGINT | NOT NULL, FK | Appointment reference |
| type | VARCHAR(20) | NOT NULL, DEFAULT 'EMAIL' | Reminder type |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Reminder status |
| scheduled_at | TIMESTAMP | NOT NULL | When to send |
| sent_at | TIMESTAMP | | When sent |
| message | VARCHAR(500) | | Reminder message |

**Foreign Keys:**
- appointment_id → appointments(id)

**Indexes:**
- idx_reminder_status_scheduled (status, scheduled_at)
- idx_reminder_appointment (appointment_id)

**Type Values:**
- EMAIL
- SMS
- IN_APP

**Status Values:**
- PENDING
- SENT
- FAILED

### audit_logs
System audit trail for compliance and security.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | FK | User who performed action |
| action | VARCHAR(50) | NOT NULL | Action performed |
| entity_type | VARCHAR(50) | NOT NULL | Entity type affected |
| entity_id | BIGINT | | Entity ID affected |
| details | VARCHAR(2000) | | Additional details |
| ip_address | VARCHAR(45) | | User IP address |
| timestamp | TIMESTAMP | NOT NULL | Action timestamp |

**Foreign Keys:**
- user_id → users(id)

**Indexes:**
- idx_audit_user (user_id)
- idx_audit_timestamp (timestamp)
- idx_audit_entity (entity_type, entity_id)

## Relationships

### One-to-Many
- User → Appointments (1:N)
- User → Feedbacks (1:N)
- ServiceProvider → Appointments (1:N)
- Location → Appointments (1:N)
- Category → ServiceProviders (1:N)
- Appointment → Reminders (1:N)
- User → AuditLogs (1:N)

### One-to-One
- Appointment → Feedback (1:1)

## Cascade Rules

### User Deletion
- Appointments: CASCADE (all user appointments deleted)
- Feedbacks: CASCADE (all user feedbacks deleted)

### Appointment Deletion
- Reminders: CASCADE (all reminders deleted)
- Feedback: CASCADE (feedback deleted)

### ServiceProvider Deletion
- Appointments: RESTRICT (cannot delete if has appointments)

### Category Deletion
- ServiceProviders: RESTRICT (cannot delete if has providers)

### Location Deletion
- Appointments: RESTRICT (cannot delete if has appointments)

## Indexes Strategy

Performance-critical queries are optimized with indexes:
- User lookups by email (unique constraint)
- Appointment queries by user, provider, date, status
- Feedback queries by rating and date
- Reminder queries by status and scheduled time
- Audit log queries by user, timestamp, entity

## Data Integrity

- All timestamps use LocalDateTime
- Soft delete via `active` flag on users, categories, locations, providers
- Hard delete available for appointments and feedbacks
- Audit logs are immutable (no updates/deletes)
- Email uniqueness enforced at database level
- Appointment-Feedback relationship is unique (one feedback per appointment)
