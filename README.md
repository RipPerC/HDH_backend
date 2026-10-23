# HdH Backend

REST API for the HdH catalog, built with Java 21, Spring Boot 4.1.0, Spring MVC, Spring Data JPA, and PostgreSQL.

## Architecture

The backend is a modular monolith. Features are grouped under `org.home.backend.catalog`; the film feature separates its web adapter, application service and output port from its Spring Data persistence adapter. Series and episode entities and repositories live in the series module. The application depends on persistence through the `FilmStore` port, not directly on Spring Data.

Angular is a separate application and container. It is not built into or served by this backend.

## Requirements

- Java 21
- Maven 3.9.16 (or use the included Maven wrapper)
- Docker Engine with the Compose plugin for containerized development

## Configuration

Database and server settings are supplied through environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8089` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/hdh_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `hdh_user` | Database user |
| `DB_PASSWORD` | empty | Database password |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Comma-separated browser origins allowed by CORS |

Do not use the empty password default outside a local environment. Database schema changes are managed by Flyway migrations in `src/main/resources/db/migration`; Hibernate validates the schema at startup and does not modify it. For an existing `data` schema without Flyway history, startup baselines it and applies the initial idempotent migration, including sequences aligned to existing IDs.

## Run with Docker Compose

The repository includes `env/dev/.env` for local development. Its password is a placeholder for local use only; replace it with a private value if needed and never reuse it outside a local environment.

Build the executable JAR into the backend image and start it with PostgreSQL:

```shell
docker compose --env-file env/dev/.env up --build -d
```

The backend is available at `http://localhost:8089`; its health endpoint is `http://localhost:8089/actuator/health`. Compose waits for PostgreSQL before starting the backend and persists database data in the `postgres-data` volume.

To include Angular, first build its image separately and set `FRONTEND_IMAGE` in `.env` to that image. The frontend image must serve its static application on port 80 and be configured so the browser can reach the API at `http://localhost:8089/v1`. Then start the frontend profile:

```shell
docker compose --env-file env/dev/.env --profile frontend up --build -d
```

By default the frontend is published on `http://localhost:4200`, which is the default allowed CORS origin. For a different public origin, set `APP_CORS_ALLOWED_ORIGINS` in `.env` to that browser origin. The Compose frontend profile uses an already-built Angular image because this repository does not contain the frontend source or its Dockerfile.

Stop the services while retaining database data:

```shell
docker compose --env-file env/dev/.env down
```

## Build the backend

Create the executable JAR without running the tests (test work is tracked separately):

```shell
./mvnw -DskipTests package
```

On Windows, use `.\mvnw.cmd -DskipTests package`.

## API

The existing film API remains under `/v1`:

- `GET /v1/film`
- `GET /v1/film/{id}`
- `POST /v1/film`
- `DELETE /v1/film/{id}`
