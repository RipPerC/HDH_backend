# Repository guide for AI agents

## Project at a glance

HdH Backend is a Java 21 / Spring Boot REST API for a film and series catalog. It is a modular monolith under `org.home.backend`; the Angular frontend is maintained separately and is not served or built here.

## Architecture and source map

- `src/main/java/org/home/backend/BackendApplication.java` is the Spring Boot entry point.
- `src/main/java/org/home/backend/catalog/film/` contains the film feature. Its web controller calls `FilmService`, which depends on the `FilmStore` output port; `FilmPersistenceAdapter` implements that port using `FilmJpaRepository`.
- `src/main/java/org/home/backend/catalog/series/` contains the `Serie` and `Episode` JPA entities and their Spring Data repositories. No series or episode HTTP controllers currently exist.
- `src/main/java/org/home/backend/config/WebConfigurer.java` configures CORS.
- `src/main/resources/application.properties` contains environment-backed application and database settings.
- `src/main/resources/db/migration/` contains Flyway schema migrations. The schema is `data`.
- `src/test/java/` contains the Spring Boot test suite.

Keep feature code within its module. For new web behavior, follow the existing film feature's adapter/application/port/persistence separation where appropriate; do not make application services depend directly on Spring Data repositories.

## Architectural pattern and request flow

The repository is a **modular monolith**. Within the film feature, the code follows a **ports-and-adapters (hexagonal) pattern**:

- The inbound web adapter (`FilmController`) translates HTTP requests into calls to the application service.
- The application layer (`FilmService`) contains film use-case behavior and depends on the `FilmStore` output port, not on a database technology.
- The outbound persistence adapter (`FilmPersistenceAdapter`) implements `FilmStore` and delegates storage operations to the Spring Data `FilmJpaRepository`.
- The repository uses the `Film` JPA entity to read and write PostgreSQL data.

Typical film request and response flow:

```mermaid
flowchart LR
    Client[HTTP client] -->|Request /v1/film| Controller[FilmController<br/>Inbound web adapter]
    Controller -->|Call use case| Service[FilmService<br/>Application layer]
    Service -->|FilmStore port| Port[FilmStore]
    Port --> Adapter[FilmPersistenceAdapter<br/>Outbound persistence adapter]
    Adapter --> Jpa[FilmJpaRepository<br/>Spring Data JPA]
    Jpa -->|Read / write Film entity| DB[(PostgreSQL<br/>data schema)]
    DB --> Jpa
    Jpa --> Adapter
    Adapter --> Service
    Service --> Controller
    Controller -->|HTTP response| Client
```

The `FilmStore` interface is the boundary that keeps application behavior independent of Spring Data. Preserve this direction when extending the film feature. The series module currently has entities and repositories only, so the film request flow does not apply to series or episodes until application and web layers are introduced.

## Existing HTTP API

The film API is rooted at `/v1`:

- `GET /v1/film` lists films; currently returns `204 No Content` when the list is empty.
- `GET /v1/film/{id}` fetches one film; currently returns `204 No Content` when no film is found.
- `POST /v1/film` creates or updates a film, selected by whether its ID is zero.
- `DELETE /v1/film/{id}` deletes a film.

The series and episode repositories are persistence-only at present; do not document them as public endpoints unless controllers are added.

## Build and test

Use the Maven wrapper so the project uses its configured Maven version:

- Windows build: `.\mvnw.cmd -DskipTests package`
- Build and run tests: `.\mvnw.cmd test`
- Unix-like build: `./mvnw -DskipTests package`
- Unix-like tests: `./mvnw test`

The test suite starts the Spring application context. Database-dependent tests require PostgreSQL configured through the environment variables documented in `README.md`.

## Data and configuration

- PostgreSQL is the runtime database. Use `docker compose --env-file env/dev/.env up --build -d` for the documented local setup.
- Hibernate is configured to validate the database schema, not create or update it.
- Make schema changes through a new Flyway migration; do not edit an already-applied migration.
- Connection and server settings come from environment variables. Never add credentials or real secrets to source control; `env/dev/.env` is a local-development template.
- Preserve the existing `/v1` API prefix and the `data` database schema unless a task explicitly changes them.

## Change guidance

- Read the existing feature and its tests before changing behavior; add or update tests for behavior changes.
- Keep changes scoped to the requested feature and preserve established package naming and formatting.
- Update `README.md` when developer setup, configuration, or public API behavior changes.
- Do not add frontend code to this repository; it is a separate application.
