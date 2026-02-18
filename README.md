<div align="center">

# 📅 Smart Appointment System

### A modern, secure, and scalable appointment management platform

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

[Features](#-features) • [Quick Start](#-quick-start) • [Documentation](#-documentation) • [Tech Stack](#-tech-stack) • [Contributing](#-contributing)

</div>

---

## 🌟 Features

<table>
<tr>
<td width="50%">

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based access control (USER/ADMIN)
- BCrypt password encryption
- Secure session management

### 📊 Appointment Management
- Complete lifecycle management
- Conflict detection
- Status tracking (Scheduled, Confirmed, Completed, etc.)
- Time slot validation

</td>
<td width="50%">

### 🚀 Performance & Scalability
- Redis caching layer
- Optimized database queries
- Horizontal scaling ready
- Docker containerization

### 📝 Audit & Compliance
- Comprehensive audit logging
- User action tracking
- IP address recording
- Compliance-ready reports

</td>
</tr>
</table>

### ✨ Additional Features

- 👥 **Service Provider Management** - Manage professionals and their services
- 📍 **Location Management** - Multiple location support
- 🏷️ **Category System** - Organize appointments by type
- ⭐ **Feedback System** - Collect and manage user reviews
- 📧 **Email Notifications** - Automated reminder system
- 📈 **Reporting Dashboard** - Admin analytics and insights
- 📖 **API Documentation** - Interactive Swagger UI

---

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 25 (for local development)
- Maven 3.9+ (for local development)

### 🐳 Run with Docker (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/smart-appointment-system.git
cd smart-appointment-system

# 2. Setup environment
cp .env.example .env.development
# Edit .env.development with your configuration

# 3. Start all services
docker-compose --env-file .env.development up --build

# 4. Access the application
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### 💻 Local Development

```bash
# 1. Start PostgreSQL and Redis
docker-compose up postgres redis -d

# 2. Run the application
mvn spring-boot:run

# 3. Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| 📖 [Architecture](docs/ARCHITECTURE.md) | System design and architecture patterns |
| 🗄️ [Database Schema](docs/DATABASE_SCHEMA.md) | Complete database structure and relationships |
| 🐳 [Docker Guide](docs/DOCKER_GUIDE.md) | Docker setup and commands |
| ⚙️ [Environment Setup](docs/ENVIRONMENT_SETUP.md) | Configuration and environment variables |
| 🔧 [Redis Cache Setup](REDIS_CACHE_SETUP.md) | Caching configuration and usage |
| 🤝 [Contributing](CONTRIBUTING.md) | How to contribute to the project |
| 🚀 [Future Features](FUTURE_FEATURES.md) | Planned enhancements and roadmap |

---

## 🛠️ Tech Stack

### Backend Framework
<p>
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot"/>
<img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security"/>
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA"/>
</p>

### Database & Cache
<p>
<img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
<img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate"/>
</p>

### Security & API
<p>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"/>
<img src="https://img.shields.io/badge/OpenAPI-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white" alt="OpenAPI"/>
</p>

### DevOps & Tools
<p>
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" alt="Git"/>
<img src="https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge&logo=lombok&logoColor=white" alt="Lombok"/>
</p>

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│          Client Applications            │
└──────────────┬──────────────────────────┘
               │ REST API
┌──────────────▼──────────────────────────┐
│         Controller Layer                │
│  • Request validation                   │
│  • Response formatting                  │
└──────────────┬──────────────────────────┘
               │ DTOs
┌──────────────▼──────────────────────────┐
│          Service Layer                  │
│  • Business logic                       │
│  • Transaction management               │
│  • Authorization                        │
└──────────────┬──────────────────────────┘
               │ Entities
┌──────────────▼──────────────────────────┐
│        Repository Layer                 │
│  • Data access                          │
│  • Query optimization                   │
└──────────────┬──────────────────────────┘
               │
    ┌──────────┴──────────┐
    ▼                     ▼
┌──────────┐         ┌──────────┐
│PostgreSQL│         │  Redis   │ 
└──────────┘         └──────────┘
```

**Key Design Patterns:**
- 🎯 Repository Pattern
- 🔄 DTO Pattern
- 🏭 Service Layer Pattern
- 🗺️ Mapper Pattern
- 🔨 Builder Pattern

---

## 📊 Project Structure

```
smart-appointment-system/
├── 📁 src/main/java/com/smartappointment/
│   ├── 🔧 config/              # Configuration classes
│   ├── 🎮 controller/          # REST endpoints
│   ├── 📦 dto/                 # Data Transfer Objects
│   ├── ⚠️ exception/           # Custom exceptions
│   ├── 🗺️ mapper/              # Entity-DTO mappers
│   ├── 📊 model/               # JPA entities & enums
│   ├── 💾 repository/          # Data access layer
│   ├── 🔐 security/            # Security components
│   ├── 💼 service/             # Business logic
│   └── 🛠️ util/                # Utility classes
│
├── 📁 docs/                    # Documentation
│   ├── ARCHITECTURE.md
│   ├── DATABASE_SCHEMA.md
│   ├── DOCKER_GUIDE.md
│   └── ENVIRONMENT_SETUP.md
│
├── 🐳 docker-compose.yml       # Docker orchestration
├── 📋 Dockerfile               # Application container
├── 📝 pom.xml                  # Maven dependencies
└── 📖 README.md                # You are here!
```

---

## 🔒 Security Features

- 🔐 **JWT Authentication** - Stateless token-based auth
- 👤 **Role-Based Access Control** - USER and ADMIN roles
- 🔑 **Password Encryption** - BCrypt hashing
- 🛡️ **CORS Protection** - Configurable CORS policies
- 📝 **Audit Logging** - Complete action tracking
- 🚫 **Input Validation** - Request validation with Bean Validation

---

## 🎯 API Endpoints

### 🔓 Public Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### 🔐 Protected Endpoints
- `GET /api/appointments` - List appointments
- `POST /api/appointments` - Create appointment
- `GET /api/categories` - List categories
- `GET /api/providers` - List service providers
- `POST /api/feedback` - Submit feedback

### 👑 Admin Only
- `GET /api/reports/dashboard` - Admin dashboard
- `POST /api/categories` - Create category
- `DELETE /api/appointments/{id}` - Delete appointment

**📖 Full API documentation available at:** `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test
mvn test -Dtest=AppointmentServiceTest
```

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Quick Contribution Steps

1. 🍴 Fork the repository
2. 🌿 Create your feature branch (`git checkout -b feature/amazing-feature`)
3. ✅ Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. 📤 Push to the branch (`git push origin feature/amazing-feature`)
5. 🎉 Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- PostgreSQL community for the robust database
- Redis team for the blazing-fast cache
- All contributors who help improve this project

---

## 📧 Contact & Support

- 📫 **Issues:** [GitHub Issues](https://github.com/yourusername/smart-appointment-system/issues)
- 💬 **Discussions:** [GitHub Discussions](https://github.com/yourusername/smart-appointment-system/discussions)
- 📖 **Documentation:** [Project Wiki](https://github.com/yourusername/smart-appointment-system/wiki)

---

<div align="center">

### ⭐ Star this repository if you find it helpful!

Made with ❤️ by the Smart Appointment System Team

</div>
