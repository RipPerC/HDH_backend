# Backend Modernization Plan

## Problem and Proposed Approach

The backend is a Java 21 application using Spring Boot 3.2.2, Maven, Spring Web, Spring Data JPA, and PostgreSQL. It is currently packaged as a WAR, marks Tomcat as a `provided` dependency, uses local database configuration in `application.properties`, has `ddl-auto=update`, and the only identified test checks that the application context loads.

Modernization will follow the requested order: first, update the platform and dependencies to compatible current stable versions while validating existing behavior; then organize the application as a modular monolith with domain boundaries and a lightweight hexagonal architecture. Splitting the backend into microservices is not proposed. The Angular frontend will run as a separate service/container, coordinated with the backend through Docker Compose. An executable JAR with an embedded server is recommended for the backend; Angular will not be packaged into the Java application.

## Tasks

1. Inventory modules, endpoints, entities, integrations, Docker/Compose configuration, and the build baseline; review the Angular-backend communication contract and confirm that no external servlet container is required.
2. Update Java, Maven, Spring Boot, dependencies, and plugins in compatible increments; resolve deprecations or breaking changes, switch to an executable JAR if no external container is required, and validate the packaging.
3. Define modules and ports/adapters based on the actual domains; incrementally migrate controllers, use cases, and persistence without changing public contracts.
4. Modernize configuration, schema, and delivery: externalize configuration and secrets, add versioned database migrations, and run Angular and the backend as separate Docker Compose services; define the required network, API URL, CORS, and health checks, then validate the build and integration.
5. Update `README.md` as the final step to reflect the architecture and modules, required versions, environment variables, migrations, local development, and running/deploying Angular and the backend with Docker Compose.

## Considerations

- Confirmed: the target is a modular monolith with lightweight hexagonal architecture, not microservices.
- Confirmed: Angular and the backend will run in separate containers coordinated by Docker Compose.
- Recommendation: run the backend as an executable JAR with an embedded server in its own container; Angular keeps its own build/static-server lifecycle in a separate container. This gives each service a clear responsibility without splitting the backend into microservices.
- The current WAR packaging and `provided` Tomcat dependency suggest a possible traditional deployment, but they are not needed for this Compose model. Review the Dockerfile/Compose setup and actual deployment workflow to rule out an external-container requirement before changing the packaging.
- Define how Angular resolves the API URL in development and production, and configure CORS or a proxy accordingly; do not hard-code the internal service name as the browser-facing URL in Angular.
- Select the latest compatible stable versions when implementation begins; review the official migration guides, available JDK, and deployment environment before pinning versions.
- The observed `application.properties` contains plaintext development credentials, verbose SQL/web logging, and `ddl-auto=update`; plan to externalize configuration and use explicit migrations without carrying those values into production configuration.
- Do not predefine domain modules; derive them from the inventory of the existing code and contracts.
- Preserve the API and current behavior; validate compilation, packaging, and Angular-backend communication/configuration in Docker Compose.
- Tests are explicitly out of scope for this plan and will be handled in a separate plan.
