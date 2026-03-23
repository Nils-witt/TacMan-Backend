# TacMan Backend

Backend for TacMan built with Spring Boot, Vaadin, and PostgreSQL.

## Tech Stack

- Java 21
- Spring Boot 4
- Vaadin 25
- PostgreSQL 18
- Liquibase migrations

## Prerequisites

- JDK 21
- Docker + Docker Compose

## Quick Start (Local)

1. Start PostgreSQL:

```bash
docker compose up -d postgres
```

2. Start the backend:

```bash
./mvnw spring-boot:run
```

3. Open the app:

- UI/Login: `http://localhost:8080/`
- API root: `http://localhost:8080/api`
- OpenAPI UI: `http://localhost:8080/swagger-ui/index.html`

## Default Local Configuration

The app reads configuration from environment variables with local defaults.

## Authentication

- Obtain JWT: `POST /api/token`
- Validate JWT: `GET /api/token` with `Authorization: Bearer <token>`
- All other `/api/**` endpoints require authentication by default.

## Password Reset

Password reset is configured through:

- `APP_BASE_URL` (link generation base URL)
- `PASSWORD_RESET_TTL_MINUTES`
- `PASSWORD_RESET_MAIL_FROM`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS`

The UI routes `/forgot-password` and `/reset-password` are publicly accessible.

## Database

- PostgreSQL is defined in `compose.yaml` and exposed on `localhost:5434`.
- Liquibase changelog is loaded from:
  `src/main/resources/db/changelog/db.changelog-master.xml`

## Useful Commands

Run tests:

```bash
./mvnw test
```

Build artifact:

```bash
./mvnw clean package
```

Build and push dev image (script in repo):

```bash
./build-dev-push.sh
```

## Project Structure

- `src/main/resources` - application config and Liquibase changelogs
- `data/` - local runtime data (database volume, overlays, photos)

## Notes

- This repository currently contains hard-coded credentials/tokens in config/build files; rotate them before production use.
- For production, set all secrets via environment variables and disable default admin credentials.
