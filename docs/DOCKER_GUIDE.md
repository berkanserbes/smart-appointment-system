# Docker Guide

## Prerequisites

- Docker 20.10+
- Docker Compose 2.0+

## Quick Start

1. Create environment file:
```bash
cp .env.example .env.development
```

2. Start all services:
```bash
docker compose --env-file .env.development up
```

3. Stop services:
```bash
docker compose down
```

## Services

### Application
- Spring Boot app on port 8080
- Multi-stage build with Maven
- Non-root user for security

### PostgreSQL
- Port 5432
- Data persisted in volume
- Health check enabled

### Redis
- Port 6379
- Data persisted in volume
- Health check enabled

## Common Commands

### Start/Stop

Start in foreground:
```bash
docker compose --env-file .env.development up
```

Start in background:
```bash
docker compose --env-file .env.development up -d
```

Stop containers:
```bash
docker compose down
```

Remove containers and volumes:
```bash
docker compose down -v
```

### Logs

View all logs:
```bash
docker compose logs
```

View specific service:
```bash
docker compose logs app
docker compose logs postgres
docker compose logs redis
```

Follow logs:
```bash
docker compose logs -f app
```

### Execute Commands

Access application container:
```bash
docker compose exec app sh
```

Access PostgreSQL:
```bash
docker compose exec postgres psql -U postgres -d smart_appointment_db
```

Access Redis:
```bash
docker compose exec redis redis-cli
```

### Service Management

Restart service:
```bash
docker compose restart app
```

Check status:
```bash
docker compose ps
```

## Database Operations

### Backup

```bash
docker compose exec postgres pg_dump -U postgres smart_appointment_db > backup.sql
```

### Restore

```bash
docker compose exec -T postgres psql -U postgres smart_appointment_db < backup.sql
```

### View Data

```bash
docker compose exec postgres psql -U postgres -d smart_appointment_db -c "SELECT * FROM users;"
```

## Production Deployment

1. Create production environment:
```bash
cp .env.example .env.production
```

2. Update docker-compose.yml env_file to `.env.production`

3. Deploy:
```bash
docker compose --env-file .env.production up -d --build
```

## Troubleshooting

### Container Won't Start

Check logs:
```bash
docker compose logs app
```

Verify configuration:
```bash
docker compose config
```

### Database Connection Failed

Check PostgreSQL health:
```bash
docker compose exec postgres pg_isready -U postgres
```

### Redis Connection Failed

Test Redis:
```bash
docker compose exec redis redis-cli ping
```

### Port Already in Use

Change ports in `.env.development`:
```env
SERVER_PORT=8081
DB_PORT=5433
REDIS_PORT=6380
```

### Clean Rebuild

```bash
docker compose down -v
docker compose build --no-cache
docker compose --env-file .env.development up
```

## Volume Management

List volumes:
```bash
docker volume ls
```

Inspect volume:
```bash
docker volume inspect smart-appointment-system_postgres-data
```

Remove unused volumes:
```bash
docker volume prune
```
