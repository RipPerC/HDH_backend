# 2026-10-24 Frontend monorepo integration

## Summary

Integrate the existing Angular application from `RipPerC/HDH_frontend` into this repository while preserving its Git history. Organize the repository as sibling `backend/` and `frontend/` applications, keep repository-level documentation and `docs/` at the root, and preserve separate application builds and containers.

## Scope

- Move the existing Spring Boot application, Maven wrapper/configuration, and backend Dockerfile into `backend/`.
- Import the Angular source under `frontend/` using Git subtree without squashing, retaining the upstream commit history.
- Keep root `compose.yaml`, `.gitignore`, `README.md`, shared `AGENTS.md`, `.github/`, and `docs/` at the repository root. Keep `env/dev/.env` available to root Compose.
- Add an Angular production container and configure root Compose to build the backend and frontend from their own directories; preserve the optional frontend Compose profile.
- Update documentation and agent guidance for the monorepo layout and per-app commands.
- Preserve the API contract and backend security configuration. Do not put OIDC client secrets in browser code. Do not add login/authentication behavior in this task.

## Decisions and findings

- Use `backend/` and `frontend/` as sibling application roots, as requested.
- Keep `docs/` outside both applications at the repository root.
- Retain root Compose and shared repository guidance; keep application-specific instructions with the corresponding app where useful.
- Build frontend assets separately and serve them from a frontend container rather than the Spring Boot JAR.
- The Angular source is Angular 14.2.8 and has `npm` scripts for build and Karma tests. It has no root `AGENTS.md` and no OIDC/authentication dependencies or token interceptor. Its production build succeeds in the available Node.js 24 environment, which is used for the container build stage.
- Both Angular environment files currently target `http://localhost:8089/v1`. The backend requires JWT bearer authentication for `/v1/**`, so the present Angular API requests do not satisfy backend authentication. Login integration requires identity-provider/client configuration and is not silently added as part of the repository layout change.
- Per user decision, OIDC login is explicitly deferred to a separate task. Until that follow-up is implemented, the current Angular API requests will receive 401 responses from the protected backend.
- `HDH_frontend` has a Dockerfile absent at its root, so a frontend container definition must be added.

## Tests and validation

- Run `npm ci`, `npm run build`, and `npm test -- --watch=false` in `frontend/`, subject to available Node/npm and browser dependencies.
- Run backend tests from `backend/`; if Docker is unavailable, run the documented JWT/security tests.
- Validate root Compose configuration and check that each service's build context resolves to its new directory.
- Confirm the subtree import retains upstream history and review the final diff for accidental secrets or API/security changes.

## Validation results

- `npm ci` completed and `npm run build` passed using Node.js 24. The install audit reported 98 dependency vulnerabilities, including 4 critical; dependency remediation was outside this integration scope.
- The Angular Karma runner launched successfully with Edge Chromium, but there are no `*.spec.ts` files and it discovered zero tests; the test command exits unsuccessfully for that reason.
- `.\mvnw.cmd -B -ntp -Dtest=JwtAudienceValidatorTests,ApiSecurityIntegrationTests test` passed from `backend/` (22 tests, no failures).
- `docker compose --env-file env/dev/.env --profile frontend config --format json` passed and resolved build contexts to `backend/` and `frontend/`. Docker image builds could not be run because the Docker daemon is unavailable in this environment.
- `git diff --check` passed. The subtree merge commit has the Angular upstream `dev` commit as its second parent, preserving its Git history.

## Implementation sequence

1. Move backend files and import the Angular subtree under `frontend/`.
2. Add frontend image configuration and update root Compose, README, and shared/app-specific agent guidance.
3. Run targeted validation and record results here.
