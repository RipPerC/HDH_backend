# Frontend guide for AI agents

## Application

This directory contains the Angular 14.2 application imported from `RipPerC/HDH_frontend`. Keep its Node/Angular build independent from the Spring Boot build in `backend/`.

## Development and validation

Use Node.js 24 with npm and run commands from `frontend/`:

- `npm ci`
- `npm start`
- `npm run build`
- `npm test -- --watch=false`

The dev environment currently calls `http://localhost:8089/v1`. The production image serves the Angular SPA and proxies its same-origin `/v1` requests to the backend Compose service.

## Authentication and configuration

The current Angular application has no OIDC login flow or access-token interceptor. The backend protects `/v1/**` with JWT bearer authentication, so API calls currently receive 401 until a separate OIDC integration is implemented. Do not add client secrets to browser code. Any future browser login must use OIDC Authorization Code with PKCE.

Do not commit local credentials or environment-specific secrets. Keep build outputs, `node_modules`, and Angular caches untracked.
