# 2026-10-23 — English-only agent work

## Objective

Make English the required language for all agent-produced work and artifacts, while preserving Spanish for direct conversations with the user. Track the change in Jira under epic `GN-2`.

## Scope and decisions

- Add the complete language policy to `AGENTS.md`.
- Add a concise reminder to `.github/copilot-instructions.md`.
- Apply the rule to repository changes, documentation, plans, pull request titles and descriptions, review comments, issue content, and commit messages.
- Create a Jira `Task` in project `GN` and associate it with epic `GN-2`.
- No application code or automated test changes are required.

## Jira

- Created [GN-795](https://ripper.atlassian.net/browse/GN-795), “Require English for Agent Work Products,” under epic `GN-2`.

## Tests

- No automated tests are applicable to this documentation-only change.
- Review the diff and verify both agent instruction files state the language policy.
- Confirm the Jira task is associated with epic `GN-2`.

## Validation

- `git diff --check`: passed.
- Confirmed the English-only rule and Spanish conversation exception appear in both agent instruction files.
- Retrieved Jira issue `GN-795` and confirmed its parent is epic `GN-2`.
