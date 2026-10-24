# Repository guide for AI agents

## Monorepo layout

- `backend/` contains the Java 21 / Spring Boot API, Maven wrapper, tests, and Flyway migrations. See [`backend/AGENTS.md`](backend/AGENTS.md) for its architecture and backend-specific conventions.
- `frontend/` contains the Angular application and its independent build. See [`frontend/AGENTS.md`](frontend/AGENTS.md) for frontend-specific commands and integration notes.
- `docs/` contains repository-wide plans and API/Postman documentation; keep it at the repository root.
- `compose.yaml` and `env/dev/.env` configure local multi-service development.

Build and run the applications independently from their respective directories. Use root Compose when running the services together.

## Shared conventions

- Keep changes scoped to the relevant application and preserve the boundary between the Angular client and Spring API.
- Preserve the `/v1` API prefix and backend authentication behavior unless the task explicitly changes them.
- Browser authentication must use OIDC Authorization Code with PKCE. Never place client secrets in Angular source or build-time browser assets.
- The existing Angular application does not yet implement OIDC login or attach access tokens. Its calls to protected `/v1/**` routes currently receive 401 responses; do not describe those calls as working until an authentication flow is implemented and tested.
- Keep credentials and local secrets out of source control. `env/dev/.env` is a development template; use private local values.
- Backend schema changes require a new Flyway migration under `backend/src/main/resources/db/migration/`; never edit an already-applied migration.
- Write repository implementation plans in English under `docs/plan/` as `YYYY-MM-DD-short-task.md`, including scope, decisions, tests, and validation.
- Write agent-produced repository artifacts in English. Direct conversation with the user may be in Spanish.

## Validation

- Backend: run the Maven wrapper from `backend/`; the full suite requires Docker for Testcontainers. The JWT/security tests can run without Docker.
- Frontend: run npm commands from `frontend/` using the committed lockfile.
- For Compose changes, validate from the repository root with `docker compose --env-file env/dev/.env config` and use the documented Compose commands in `README.md`.
