# Copilot instructions

Use the repository guide in [`AGENTS.md`](../AGENTS.md) for the project architecture, commands, API behavior, and change conventions.

Write all agent-produced work and artifacts in English, including pull request content; only direct conversations with the user may be in Spanish.

Before changing code, inspect the relevant feature and tests. Keep changes scoped, preserve the film feature's application-port-persistence boundaries, and use Flyway migrations for database schema changes. Do not add credentials or real secrets to the repository.

Write repository implementation plans in English and save them in `docs/plan/` as `YYYY-MM-DD-short-task.md`, with a date-first title and concise task summary. Include scope, key decisions, tests, and validation; revise the plan when the implementation materially changes.
