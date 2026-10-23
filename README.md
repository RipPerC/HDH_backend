# HdH Backend

REST API for the HdH catalog, built with Java 21, Spring Boot 4.1.0, Spring MVC, Spring Data JPA, and PostgreSQL.

For repository architecture, development commands, and contributor guidance, see [AGENTS.md](AGENTS.md).

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
| `OIDC_ISSUER_URI` | required | JWT issuer URI configured by the identity provider |
| `OIDC_JWKS_URI` | required | Provider JWKS endpoint used to verify JWT signatures |
| `OIDC_AUDIENCE` | required | Audience claim expected in API access tokens |

Do not use the empty password default outside a local environment. Database schema changes are managed by Flyway migrations in `src/main/resources/db/migration`; Hibernate validates the schema at startup and does not modify it. For an existing `data` schema without Flyway history, startup baselines it and applies the initial idempotent migration, including sequences aligned to existing IDs.

The OIDC values in `env/dev/.env` are examples only; replace them with values for a configured identity provider before using protected endpoints. The backend does not issue tokens or manage user accounts. Angular is a public client and must use OAuth 2.0 / OpenID Connect Authorization Code with PKCE; never put a client secret in browser code. After login, send the access token in `Authorization: Bearer <access-token>`. CORS only controls browser cross-origin access; it does not authenticate callers.

## Run with Docker Compose

The repository includes `env/dev/.env` for local development. Its password is a placeholder for local use only; replace it with a private value if needed and never reuse it outside a local environment.

Build the executable JAR into the backend image and start it with PostgreSQL:

```shell
docker compose --env-file env/dev/.env up --build -d
```

The backend is available at `http://localhost:8089`; its health endpoint is `http://localhost:8089/actuator/health`. Compose waits for PostgreSQL before starting the backend and persists database data in the `postgres-data` volume.

Set `OIDC_ISSUER_URI`, `OIDC_JWKS_URI`, and `OIDC_AUDIENCE` in the Compose environment before starting the backend. The health endpoint remains public; all other API paths require a signed, unexpired token with the configured issuer and audience.

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

Every `/v1/**` endpoint requires a valid JWT access token. The API returns `application/problem+json` Problem Details (RFC 9457) for errors:

| Status | Meaning |
| --- | --- |
| 400 | Malformed or invalid request |
| 401 | Missing, invalid, or expired access token |
| 403 | Authenticated caller is forbidden |
| 404 | Route or requested film does not exist |
| 405 | HTTP method is not supported for the route |
| 415 | Request media type is not supported |
| 500 | Unexpected server error; internal details are not exposed |

Example:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "The requested resource was not found.",
  "instance": "/v1/film/42"
}
```

An empty film list remains `204 No Content`; a film lookup or deletion for a missing ID returns `404`.

### Postman collection

Import [`postman/HDH Backend.postman_collection.json`](postman/HDH%20Backend.postman_collection.json) into Postman. Set `baseUrl` and `frontendOrigin`, then set `accessToken` to a valid access token from the configured test identity provider. The collection includes unauthenticated and invalid/expired token checks, CRUD requests, HTTP error cases, public health, and a CORS preflight request. Set `expiredToken` to an expired token issued by that provider to run the expiration case. Run the CRUD requests in order: creating a film stores its ID for the later get, update, and delete requests. The collection includes assertions for expected status codes and Problem Details responses.

The API currently has no role-protected endpoint, so a 403 authorization decision is covered by automated tests for the configured forbidden-response handler rather than a Postman request.

### Automated tests

The tests require Docker to be running. `BackendApplicationTests` starts a disposable PostgreSQL 17 container with Testcontainers, runs the application context and Flyway migrations against it, then removes the container when the test class finishes. No local PostgreSQL setup or OIDC provider is needed:

```shell
./mvnw test
```

The security and HTTP behavior tests use a mocked JWT decoder and do not need the PostgreSQL container; run only those with `./mvnw -Dtest=JwtAudienceValidatorTests,ApiSecurityIntegrationTests test`.
