# Environment Configuration

## Setup Instructions

### 1. Create Environment File

For development:
```bash
cp .env.example .env.development
```

For production:
```bash
cp .env.example .env.production
```

### 2. Configure Variables

Edit `.env.development` or `.env.production` with your values:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=smart_appointment_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your_secret_key_minimum_256_bits
JWT_EXPIRATION=1800000

# Mail (Optional)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Admin Seed User (created automatically on first startup)
ADMIN_EMAIL=admin@smartappointment.com
ADMIN_PASSWORD=your_admin_password
```

### 3. Run Application

With Docker:
```bash
docker-compose up --build
```

Without Docker (local):
```bash
# Windows
mvn spring-boot:run

# Linux/Mac
export $(cat .env.development | xargs) && mvn spring-boot:run
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Application port | 8080 |
| DB_HOST | PostgreSQL host | localhost |
| DB_PORT | PostgreSQL port | 5432 |
| DB_NAME | Database name | smart_appointment_db |
| DB_USERNAME | Database username | postgres |
| DB_PASSWORD | Database password | - |
| REDIS_HOST | Redis host | localhost |
| REDIS_PORT | Redis port | 6379 |
| JWT_SECRET | JWT secret key (min 256 bit) | - |
| JWT_EXPIRATION | Token expiration (ms) | 1800000 |
| MAIL_HOST | SMTP host | smtp.gmail.com |
| MAIL_PORT | SMTP port | 587 |
| MAIL_USERNAME | Email address | - |
| MAIL_PASSWORD | Email app password | - |
| ADMIN_EMAIL | Admin seed user email | admin@smartappointment.com |
| ADMIN_PASSWORD | Admin seed user password | - |

## Security Notes

### Generate JWT Secret

Linux/Mac:
```bash
openssl rand -base64 64
```

Windows PowerShell:
```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

Or use any online random string generator

## Troubleshooting

### Environment variables not loading

Check that:
- `.env.development` exists in project root
- Restart containers: `docker compose down && docker compose --env-file .env.development up`

### JWT secret error

Ensure:
- `JWT_SECRET` is defined in `.env.development`
- Secret is at least 256 bits (32 characters)
- No spaces or special characters causing parsing issues
