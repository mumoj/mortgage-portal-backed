# Mortgage Portal Backend

![CI/CD Pipeline](https://github.com/username/mortgage-portal-backend/workflows/CI/CD%20Pipeline/badge.svg)
![Coverage](https://codecov.io/gh/username/mortgage-portal-backend/branch/main/graph/badge.svg)

A Spring Boot microservice for mortgage application management with real-time event publishing and comprehensive API documentation.

## Features

- **RESTful API** - Complete CRUD operations for mortgage applications
- **Role-based Access Control** - JWT authentication with APPLICANT/OFFICER roles
- **Event-Driven Architecture** - Kafka integration for real-time notifications
- **Document Management** - S3-style presigned URL handling
- **Comprehensive Testing** - Unit, integration, and E2E test coverage
- **Production Ready** - Docker, observability, security, and CI/CD

## Tech Stack

- **Framework**: Spring Boot 3.2, Spring Security, Spring Data JPA
- **Database**: PostgreSQL with Flyway migrations
- **Messaging**: Apache Kafka
- **Authentication**: JWT tokens
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Testcontainers, Mockito
- **Build**: Maven, Docker multi-stage builds
- **Observability**: OpenTelemetry, structured logging

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### One-Command Setup
```bash
git clone https://github.com/username/mortgage-portal-backend.git
cd mortgage-portal-backend
docker-compose up
```

The application will be available at:
- **API**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Kafka UI**: http://localhost:8081
- **Health Check**: http://localhost:8080/actuator/health

### Local Development
```bash
# Start infrastructure
docker-compose up postgres kafka zookeeper kafka-ui -d

# Run application
cd backend
./mvnw spring-boot:run --spring.profiles.active=dev

# Run tests
./mvnw test

# Generate coverage report
./mvnw jacoco:report
```

## API Usage

### Authentication
All endpoints require JWT Bearer token except health checks:

```bash
curl -H "Authorization: Bearer <jwt-token>" \
     http://localhost:8080/api/v1/applications
```

### Create Application (APPLICANT role)
```bash
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Authorization: Bearer <jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nationalId": "123456789",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@email.com",
    "phoneNumber": "+1234567890",
    "loanAmount": 250000,
    "annualIncome": 75000,
    "employmentType": "FULL_TIME",
    "propertyAddress": "123 Main St",
    "propertyValue": 300000
  }'
```

### Get Applications with Filters
```bash
# List all applications (OFFICER)
curl "http://localhost:8080/api/v1/applications?page=0&size=20&status=PENDING" \
  -H "Authorization: Bearer <jwt-token>"

# Get specific application
curl http://localhost:8080/api/v1/applications/1 \
  -H "Authorization: Bearer <jwt-token>"
```

### Make Decision (OFFICER role)
```bash
curl -X PATCH http://localhost:8080/api/v1/applications/1/decision \
  -H "Authorization: Bearer <jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "decisionType": "APPROVED",
    "comments": "Excellent credit history and stable income"
  }'
```

## Event Schema

### Kafka Topic: `loan.applications`

```json
{
  "applicationId": "123",
  "eventType": "CREATED|UPDATED|DELETED",
  "payload": {
    "id": 123,
    "nationalId": "123456789",
    "status": "PENDING",
    "loanAmount": 250000,
    ...
  },
  "traceId": "uuid",
  "version": "1.0",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Database Schema

### Applications Table
```sql
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    national_id VARCHAR(12) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    loan_amount DECIMAL(15,2) NOT NULL,
    annual_income DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applicant_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## Configuration

### Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mortgage_db
DB_USER=postgres
DB_PASSWORD=password

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Security
JWT_SECRET=mySecretKeyForJWT1234567890

# AWS (for document storage)
AWS_REGION=us-east-1
S3_BUCKET=mortgage-documents

# Logging
LOG_LEVEL=INFO
```

### Profiles
- **dev**: Development with debug logging and relaxed security
- **prod**: Production with strict security and performance optimizations

## Testing

### Test Coverage
- **Unit Tests**: Services, repositories, mappers (>80% coverage)
- **Integration Tests**: Database operations with Testcontainers
- **Contract Tests**: OpenAPI specification validation
- **E2E Tests**: Postman collections via Newman

```bash
# Run all tests
./mvnw verify

# Run specific test categories
./mvnw test -Dtest=**/*ServiceTest
./mvnw test -Dtest=**/*IntegrationTest

# Performance test with load
newman run postman/Load-Test-Collection.json -n 100 --parallel
```

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │────│   API Gateway   │────│  Load Balancer  │
│   (React)       │    │   (Spring GW)   │    │     (nginx)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                         ┌─────────────────┐
                         │  Mortgage API   │
                         │ (Spring Boot)   │
                         └─────────────────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
     ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
     │   PostgreSQL    │ │     Kafka       │ │       S3        │
     │   (Database)    │ │  (Events)       │ │  (Documents)    │
     └─────────────────┘ └─────────────────┘ └─────────────────┘
```

## Security

- **OWASP Top 10** mitigations implemented
- **CORS** configured for cross-origin requests  
- **Rate limiting** with Bucket4j (100 requests/minute)
- **JWT validation** with role-based access control
- **Input validation** with Bean Validation
- **SQL injection** prevention via JPA/Hibernate
- **Secrets management** via environment variables

## Observability

### Structured Logging
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "level": "INFO", 
  "logger": "com.bank.mortgage.service.ApplicationService",
  "message": "Application created successfully",
  "traceId": "abc123",
  "spanId": "def456",
  "userId": "john.doe",
  "applicationId": "789"
}
```

### Metrics & Tracing
- **OpenTelemetry** for distributed tracing
- **Micrometer** metrics exported to Prometheus
- **Health checks** available at `/actuator/health`
- **Custom business metrics** for application processing

## Deployment

### Docker
```bash
# Build image
docker build -t mortgage-portal:latest ./backend

# Run with compose
docker-compose up -d

# Scale horizontally  
docker-compose up --scale app=3
```

### Cloud Deployment
```bash
# Deploy to Railway
railway up

# Deploy to Fly.io  
fly deploy

# Deploy to Azure
az webapp up --sku B1 --name mortgage-portal
```

## API Documentation

Interactive API documentation is available at `/swagger-ui.html` when the application is running.

The OpenAPI 3.0 specification is available at `/v3/api-docs` and exported to `openapi.yaml`.

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)  
3. Follow conventional commits (`feat:`, `fix:`, `docs:`)
4. Ensure tests pass (`./mvnw verify`)
5. Submit pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.