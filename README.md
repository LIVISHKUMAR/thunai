# thunai

SMS Commerce Engine — Multi-tenant SaaS platform that transforms any mobile phone into a shopping device using plain SMS.

## Architecture

- Backend: Java 21 + Spring Boot 3.3 microservices
- Frontend: React + TypeScript + Vite
- Database: PostgreSQL 16 with Row-Level Security
- Messaging: RabbitMQ
- Cache: Redis
- Service Discovery: Consul
- API Gateway: Spring Cloud Gateway
- Identity: Keycloak

## Modules

- `thunai-common` — shared DTOs, exceptions, enums
- `thunai-gateway` — API Gateway + auth
- `thunai-tenant-service` — tenants, stores, products, catalog
- `thunai-order-service` — carts, orders, payments
- `thunai-customer-service` — customers, sessions, RBAC
- `thunai-notification-service` — SMS send/receive, templates
- `thunai-frontend` — React dashboard

## Quick Start

### Prerequisites

- Java 21, Maven 3.9+, Node.js 20+, Docker + Docker Compose

### Start with Docker Compose

```bash
cp .env.example .env
docker compose up -d --build
```

Services:
- Gateway: http://localhost:8080
- Frontend: http://localhost:3000
- Keycloak: http://localhost:8085
- Consul: http://localhost:8500
- RabbitMQ: http://localhost:15672

### Build Java services

```bash
mvn clean package -DskipTests
```

### Run Frontend

```bash
cd thunai-frontend
npm install
npm run dev
```

## Documentation

See `plain_doc/` for full architecture, SMS flows, dashboard UX, and integration guides.
