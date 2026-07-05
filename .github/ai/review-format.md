# AI Review Output Format

This file defines the required output format for AI reviewers.

## Gemini Output Format

Gemini must use this format.

```md
## Gemini Spec Review Result

### Decision

[APPROVE] or [REJECT]

### Target Ticket

- Ticket ID:
- Ticket Name:
- Source Branch:
- Target Branch:

### Checklist Verification

| Checklist Item | Status | Evidence |
|---|---|---|
| item | PASS/FAIL/PARTIAL | file, diff, or docs evidence |

### Specification Compliance

| Area | Result | Notes |
|---|---|---|
| Ticket Scope | PASS/FAIL/PARTIAL |  |
| API Spec | PASS/FAIL/PARTIAL |  |
| SRS / Business Rules | PASS/FAIL/PARTIAL |  |
| ERD / Data Model | PASS/FAIL/PARTIAL |  |
| Backend Design | PASS/FAIL/PARTIAL |  |
| Test Strategy | PASS/FAIL/PARTIAL |  |

### Blocking Issues

List only issues that must be fixed before merge.

Use this format:

- [BLOCKER] Description
  - Expected:
  - Actual:
  - Relevant docs:
  - Suggested fix:

If there are no blocking issues, write:

- None

### Non-blocking Suggestions

List optional improvements only.

If none, write:

- None

### Final PR Comment

Write a concise Korean GitHub PR review comment.

It must include either `[APPROVE]` or `[REJECT]`.
```

## Claude Output Format

Claude must use this format.

```md
## Claude CTO Review Result

### Decision

[MERGE_READY] or [CHANGES_REQUESTED]

### Summary

Brief Korean summary of the PR quality.

### Review Checklist

| Area | Result | Notes |
|---|---|---|
| Correctness | PASS/FAIL/PARTIAL |  |
| Architecture | PASS/FAIL/PARTIAL |  |
| Security | PASS/FAIL/PARTIAL |  |
| Transaction / Data Consistency | PASS/FAIL/PARTIAL |  |
| Error Handling | PASS/FAIL/PARTIAL |  |
| Test Quality | PASS/FAIL/PARTIAL |  |
| Maintainability | PASS/FAIL/PARTIAL |  |
| Performance | PASS/FAIL/PARTIAL |  |
| Operational Risk | PASS/FAIL/PARTIAL |  |

### Blocking Issues

List only issues that must be fixed before merge.

Use this format:

- [BLOCKER] Description
  - Why it matters:
  - Evidence:
  - Suggested fix:

If there are no blocking issues, write:

- None

### Recommended Improvements

List non-blocking improvements.

If none, write:

- None

### Questions

Ask only if clarification is genuinely required.

If none, write:

- None

### Final PR Comment

Write a concise Korean GitHub PR review comment.

It must include either `[MERGE_READY]` or `[CHANGES_REQUESTED]`.
```

## General Rules

- Blocking issues must be concrete.
- Blocking issues must include evidence.
- Do not create blockers from personal preference.
- Do not include long explanations unless necessary.
- Final PR comments should be concise and actionable.
