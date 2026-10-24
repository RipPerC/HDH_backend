# HdH Monorepo

HdH film and series catalog, organized as a monorepo with an Angular frontend and a Spring Boot REST API.

For shared repository conventions, see [AGENTS.md](AGENTS.md). Application-specific guidance is in [`backend/AGENTS.md`](backend/AGENTS.md) and [`frontend/AGENTS.md`](frontend/AGENTS.md).

## Repository layout

| Directory | Contents |
| --- | --- |
| `backend/` | Java 21 / Spring Boot API, Maven wrapper, tests, Flyway migrations, and backend Dockerfile |
| `frontend/` | Angular 14 application and frontend Dockerfile |
| `docs/` | Repository-level plans and the Postman collection |
| `compose.yaml` | Local database, backend, and optional frontend services |

The applications build and run independently. The backend remains a modular monolith; its film feature separates the web adapter, application service and output port from its Spring Data persistence adapter.

The frontend was imported from [`RipPerC/HDH_frontend`](https://github.com/RipPerC/HDH_frontend) with its Git history. The `frontend-upstream` remote tracks its `dev` branch; to bring in future upstream changes, run `git fetch frontend-upstream` followed by `git subtree pull --prefix=frontend frontend-upstream dev`.

## Requirements

- Java 21
- Maven 3.9.16 (or use the included Maven wrapper)
- Node.js 24 and npm for the existing Angular 14.2 application
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

Do not use the empty password default outside a local environment. Database schema changes are managed by Flyway migrations in `backend/src/main/resources/db/migration`; Hibernate validates the schema at startup and does not modify it. For an existing `data` schema without Flyway history, startup baselines it and applies the initial idempotent migration, including sequences aligned to existing IDs.

The OIDC values in `env/dev/.env` are examples only; replace them with values for a configured identity provider before using protected endpoints. The backend does not issue tokens or manage user accounts. Angular is a public client and must use OAuth 2.0 / OpenID Connect Authorization Code with PKCE; never put a client secret in browser code. After login, send the access token in `Authorization: Bearer <access-token>`. CORS only controls browser cross-origin access; it does not authenticate callers.

## Run with Docker Compose

The imported Angular app does not yet include OIDC login or access-token handling. The backend protects `/v1/**`, so API operations from Angular return `401 Unauthorized` until authentication integration is implemented.

The repository includes `env/dev/.env` for local development. Its password is a placeholder for local use only; replace it with a private value if needed and never reuse it outside a local environment.

Build the backend image and start it with PostgreSQL:

```shell
docker compose --env-file env/dev/.env up --build -d
```

The backend is available at `http://localhost:8089`; its health endpoint is `http://localhost:8089/actuator/health`. Compose waits for PostgreSQL before starting the backend and persists database data in the `postgres-data` volume.

Set `OIDC_ISSUER_URI`, `OIDC_JWKS_URI`, and `OIDC_AUDIENCE` in the Compose environment before starting the backend. The health endpoint remains public; all other API paths require a signed, unexpired token with the configured issuer and audience.

To also build and start Angular, enable the frontend profile. Compose builds the frontend image from `frontend/`; its web server serves the SPA and proxies `/v1` to the backend container:

```shell
docker compose --env-file env/dev/.env --profile frontend up --build -d
```

The frontend is published at `http://localhost:4200`. For a different browser origin, set `APP_CORS_ALLOWED_ORIGINS` in `env/dev/.env` to that origin. The frontend's API proxy uses the internal Compose service name and does not require exposing the backend hostname to the browser.

Stop the services while retaining database data:

```shell
docker compose --env-file env/dev/.env down
```

## Build and test the backend

Run the Maven wrapper from the backend directory:

```shell
cd backend
./mvnw -DskipTests package
```

On Windows, run `cd backend` followed by `.\mvnw.cmd -DskipTests package`.

The complete test suite uses Testcontainers and requires Docker:

```shell
cd backend
./mvnw test
```

On Windows, use `.\mvnw.cmd test`. Security and HTTP tests can run without PostgreSQL or Docker using `./mvnw -Dtest=JwtAudienceValidatorTests,ApiSecurityIntegrationTests test` (Windows: `.\mvnw.cmd -Dtest=JwtAudienceValidatorTests,ApiSecurityIntegrationTests test`).

## Build and test the frontend

Run the Angular commands from `frontend/`:

```shell
cd frontend
npm ci
npm run build
npm test -- --watch=false
```

The imported frontend currently contains no `*.spec.ts` files, so Karma discovers zero tests and exits unsuccessfully until frontend tests are added.

For local development, start the backend and database with Compose, then run `npm start` in `frontend/`. The Angular development environment calls `http://localhost:8089/v1` directly; the production container uses the same-origin `/v1` proxy.

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

Import [`docs/postman/HDH Backend.postman_collection.json`](docs/postman/HDH%20Backend.postman_collection.json) into Postman. Set `baseUrl` and `frontendOrigin`, then set `accessToken` to a valid access token from the configured test identity provider. The collection includes unauthenticated and invalid/expired token checks, CRUD requests, HTTP error cases, public health, and a CORS preflight request. Set `expiredToken` to an expired token issued by that provider to run the expiration case. Run the CRUD requests in order: creating a film stores its ID for the later get, update, and delete requests. The collection includes assertions for expected status codes and Problem Details responses.

The API currently has no role-protected endpoint, so a 403 authorization decision is covered by automated tests for the configured forbidden-response handler rather than a Postman request.
