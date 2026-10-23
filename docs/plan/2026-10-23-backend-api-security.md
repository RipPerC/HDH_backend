# 2026-10-23 — Backend API security with OIDC/JWT

## Objective

Require authentication for `/v1/**`, standardize API errors with Problem Details (RFC 9457), and document how to configure and test the Angular integration.

## Changes implemented

- Configured Spring Security as a stateless OAuth 2.0 Resource Server. API endpoints require a signed JWT with valid issuer, expiration, and audience; the Actuator health endpoint remains public.
- Added `OIDC_ISSUER_URI`, `OIDC_JWKS_URI`, and `OIDC_AUDIENCE` environment configuration and passed them through Docker Compose. Values in `env/dev/.env` are examples and must be replaced with those from the selected OIDC provider.
- Aligned CORS with the security filter chain to allow `Authorization` and preflight requests from configured origins. CORS is not used as authentication.
- Added `application/problem+json` responses for security and MVC errors: 400, 401, 403, 404, 405, 415, and 500. HTTP 500 responses do not expose internal details.
- Requests to retrieve or delete a nonexistent film return 404; an empty film list continues to return 204.
- Added an importable Postman collection covering health, film CRUD, authentication errors, missing resources, HTTP errors, and CORS preflight. HTTP 403 is covered by automated tests because no role-restricted routes currently exist.
- Updated `README.md` and `AGENTS.md` with the API contract, OIDC configuration, Angular Authorization Code + PKCE flow, tests, and Postman collection.

## Tests and temporary database

- `BackendApplicationTests` uses Testcontainers to create PostgreSQL 17, start the Spring application context, run Flyway migrations, and remove the container when the test class finishes. The full suite requires Docker but does not require a local PostgreSQL installation.
- Added `spring-boot-starter-flyway`, which is required to activate Flyway migrations with Spring Boot 4.
- HTTP security tests use a mocked `JwtDecoder` and do not require an OIDC provider or database container.
- Coverage includes missing, invalid, expired, and valid JWTs; 401/403 responses; 400/404/405/415/500 errors; public health; CORS/preflight; and regression checks for 200/201/202/204 responses.

## Verification

- `.\mvnw.cmd test`: all 23 tests pass, including the application-context test against temporary PostgreSQL 17 with Flyway.
- `.\mvnw.cmd -Dtest=JwtAudienceValidatorTests,ApiSecurityIntegrationTests test`: all 22 tests pass without PostgreSQL or Docker.
- `docker compose --env-file env/dev/.env config --quiet`: valid configuration.
- The Postman collection JSON parsed successfully.
