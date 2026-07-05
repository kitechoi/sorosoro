# SOROSORO Documentation

This repository follows a Docs-first Development process.

All AI agents and human contributors must treat this document as the entry point for understanding the project.

## Project Overview

SOROSORO is a personal sewing diary service.

The backend is implemented with:

- Java 17
- Spring Boot 3.x
- Gradle
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT authentication
- Docker Compose

## Documentation Reading Order

When an AI agent or contributor needs to understand the project, read the documents in this order:

1. `01_PRD.md`
2. `02_SRS.md`
3. `03_ADR.md`
4. `04_ERD.md`
5. `05_Backend_Design.md`
6. `06_Architecture.md`
7. `07.API.md`
8. `08_Implementation.md`
9. `09_Test Strategy.md`
10. `tickets.md`

`10_Portfolio README.md` and `11_Wireframe.md` are supporting documents.
They should be referenced only when needed.

## Source of Truth

Use the following document as the primary source for each type of decision.

| Area | Source of Truth |
|---|---|
| Product purpose and user value | `01_PRD.md` |
| Functional and business requirements | `02_SRS.md` |
| Architecture decisions and trade-offs | `03_ADR.md` |
| Database model, entities, relationships, constraints | `04_ERD.md` |
| Backend layering, validation, service responsibility | `05_Backend_Design.md` |
| System architecture and dependency direction | `06_Architecture.md` |
| API paths, methods, requests, responses, status codes | `07.API.md` |
| Implementation sequence and technical notes | `08_Implementation.md` |
| Testing scope and expectations | `09_Test Strategy.md` |
| Ticket scope and implementation checklist | `tickets.md` |

## Conflict Resolution Priority

When documents conflict, follow this priority:

1. `tickets.md`
2. `07.API.md`
3. `02_SRS.md`
4. `04_ERD.md`
5. `05_Backend_Design.md`
6. `06_Architecture.md`
7. `03_ADR.md`
8. `01_PRD.md`
9. `08_Implementation.md`
10. `09_Test Strategy.md`

If the conflict cannot be resolved from the documents, do not guess.
Leave a clear question for the human maintainer.

## AI Agent Roles

### Codex

Codex is the main implementation agent.

Codex should:

- Implement one ticket per branch.
- Read `docs/README.md` first.
- Use `docs/tickets.md` to identify the target ticket.
- Use only the relevant documents for the ticket.
- Avoid unrelated refactoring.
- Avoid implementing future-scope features.
- Open a Pull Request using `.github/PULL_REQUEST_TEMPLATE.md`.

### Gemini

Gemini is the specification compliance reviewer.

Gemini should:

- Review PRs from `feature/ticket-*` branches into `pre-dev`.
- Check whether the PR matches the target ticket and project documentation.
- Focus on requirements, API contract, ERD consistency, ownership rules, validation rules, and required tests.
- Avoid subjective code-style review.
- Approve only when there are no blocking specification mismatches.

### Claude

Claude is the CTO-level code reviewer.

Claude should:

- Review PRs from `pre-dev` into `dev`.
- Assume Gemini has already checked specification compliance.
- Focus on code quality, maintainability, architecture, security, transactions, tests, performance, and operational risk.
- Avoid repeating the full specification review.
- Request changes only when there is a concrete correctness, security, maintainability, testing, or operational risk.

## Branch and PR Flow

```text
feature/ticket-N
  ↓ PR
pre-dev
  - Gemini: specification compliance review
  - CI: build/test/lint
  - merge after approval and checks pass

pre-dev
  ↓ PR
dev
  - Claude: CTO-level code quality review
  - CI: build/test/lint
  - merge after approval and checks pass
```

## General Development Rule

The documentation is the single source of truth.

Prompts should define how AI agents think and review.
Prompts should not duplicate large amounts of project-specific business rules.
Project-specific rules must live in `docs/`.
