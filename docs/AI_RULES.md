# AI Rules

This document defines common rules for all AI agents working on this repository.

All agents must read this file before implementing, reviewing, or modifying code.

## Core Principle

Docs are the single source of truth.

AI agents must not invent undocumented requirements, business rules, API behavior, database constraints, or architecture decisions.

## Common Rules

- Read `docs/README.md` first.
- Follow the document priority defined in `docs/README.md`.
- Work on one ticket at a time.
- One PR should implement one ticket.
- Do not expand the ticket scope.
- Do not implement future-scope features unless the ticket explicitly requires them.
- Do not silently change documented behavior.
- Do not perform broad unrelated refactoring.
- Do not change API contracts unless the related docs are updated.
- Do not change database schema without a Flyway migration.
- Do not expose internal entities directly through API responses.
- Do not hardcode secrets, credentials, local-only paths, or environment-specific values.
- Do not rely on client-provided user identifiers for ownership or authorization decisions.
- When documentation is ambiguous, leave a clear question instead of guessing.

## Implementation Rules for Codex

Codex should:

1. Identify the target ticket from `docs/tickets.md`.
2. Create or work on a feature branch for that ticket.
3. Read only the documents relevant to the ticket after reading `docs/README.md`.
4. Implement the smallest complete change that satisfies the ticket.
5. Add or update tests required by the ticket and `docs/09_Test Strategy.md`.
6. Avoid unrelated formatting or refactoring changes.
7. Open a PR using `.github/PULL_REQUEST_TEMPLATE.md`.

Codex should not:

- Implement multiple unrelated tickets in one PR.
- Modify documentation to hide an implementation mismatch.
- Remove tests to make CI pass.
- Add new abstractions without a clear need.
- Change business behavior without an explicit ticket requirement.

## Review Rules for Gemini

Gemini reviews specification compliance.

Gemini should verify:

- The PR corresponds to the target ticket.
- The ticket checklist is satisfied.
- API behavior matches `docs/07.API.md`.
- Business rules match `docs/02_SRS.md`.
- Database/entity usage matches `docs/04_ERD.md`.
- Backend responsibilities match `docs/05_Backend_Design.md`.
- Tests satisfy the ticket and `docs/09_Test Strategy.md`.
- The PR does not include unrelated scope.

Gemini should not:

- Review subjective code style.
- Request refactoring unless it is required by the specification.
- Re-design architecture based on preference.
- Approve undocumented behavior changes.

## Review Rules for Claude

Claude reviews CTO-level code quality after Gemini has approved specification compliance.

Claude should verify:

- Architecture and layering are maintainable.
- Security and ownership checks are safe.
- Transaction boundaries are appropriate.
- Error handling is consistent.
- Tests cover meaningful behavior.
- Database access patterns are reasonable.
- The implementation does not make future tickets unnecessarily harder.

Claude should not:

- Re-check the entire product specification.
- Block PRs for minor naming or formatting issues.
- Request large rewrites without concrete risk.
- Expand the feature scope.

## Decision Rule

When unsure:

- If the issue is a real correctness, security, data consistency, test, or operational risk, block the PR.
- If the issue is only a preference or minor improvement, leave it as a non-blocking suggestion.
- If the docs are unclear, ask for clarification.
