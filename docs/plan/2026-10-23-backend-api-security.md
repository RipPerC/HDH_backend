# 2026-10-23 — Seguridad de API backend con OIDC/JWT

## Objetivo

Proteger la API del backend para que `/v1/**` requiera autenticación, unificar las respuestas de error con Problem Details (RFC 9457) y documentar cómo configurar y probar la integración con el frontend Angular.

## Cambios realizados

- Se configuró Spring Security como OAuth 2.0 Resource Server sin sesiones. Los endpoints requieren un JWT firmado con issuer, expiración y audience válidos; el health check de Actuator permanece público.
- Se configuraron `OIDC_ISSUER_URI`, `OIDC_JWKS_URI` y `OIDC_AUDIENCE` por entorno y se pasaron mediante Docker Compose. Los valores de `env/dev/.env` son de ejemplo y deben sustituirse por los del proveedor OIDC utilizado.
- Se alineó CORS con la cadena de seguridad para admitir `Authorization` y preflight desde los orígenes configurados. CORS no se usa como mecanismo de autenticación.
- Se añadieron respuestas `application/problem+json` para errores de seguridad y MVC: 400, 401, 403, 404, 405, 415 y 500. Los errores 500 no exponen detalles internos.
- La consulta o eliminación de una película inexistente devuelve 404; la lista de películas vacía conserva 204.
- Se añadió una colección Postman importable con health, CRUD de películas, errores de autenticación, recursos inexistentes, errores HTTP y preflight CORS. El 403 se valida en pruebas automatizadas porque actualmente no hay rutas con reglas de roles.
- Se actualizaron `README.md` y `AGENTS.md` con el contrato de API, la configuración OIDC, el uso desde Angular con Authorization Code + PKCE, las pruebas y la colección Postman.

## Pruebas y base de datos temporal

- `BackendApplicationTests` usa Testcontainers para crear PostgreSQL 17, ejecutar el contexto de Spring y las migraciones Flyway, y destruir el contenedor al finalizar. La suite completa requiere Docker activo; no requiere PostgreSQL local.
- Se añadió `spring-boot-starter-flyway`, necesario para activar la ejecución de las migraciones Flyway con Spring Boot 4.
- Las pruebas de seguridad HTTP usan un `JwtDecoder` simulado y no requieren proveedor OIDC ni contenedor de base de datos.
- Cobertura: JWT ausente, inválido, expirado y válido; respuestas 401/403; errores 400/404/405/415/500; health público; CORS/preflight; y regresión de respuestas 200/201/202/204.

## Verificación

- `.\mvnw.cmd test`: 23 pruebas pasan, incluida la prueba de contexto con PostgreSQL 17 temporal y Flyway.
- `.\mvnw.cmd -Dtest=JwtAudienceValidatorTests,ApiSecurityIntegrationTests test`: 22 pruebas pasan sin requerir PostgreSQL ni Docker.
- `docker compose --env-file env/dev/.env config --quiet`: configuración válida.
- La colección JSON de Postman se parseó correctamente.
