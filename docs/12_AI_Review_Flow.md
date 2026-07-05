# AI Review Flow

This document is the source of truth for the SOROSORO AI review workflow.

The goal is to use GPT/Claude Pro subscriptions, low-cost Gemini API review, Codex implementation, and GitHub Actions automation while minimizing Claude API usage and preventing endless AI review loops.

## 1. Roles

| Role | Responsibility |
|---|---|
| GPT | Designs documentation, prompts, and operating strategy. |
| Claude Fable / Opus | Performs initial architecture audits, rare high-difficulty decisions, and sprint or milestone audits. It is not used as a constant reviewer. |
| Codex Builder | Implements one ticket at a time, writes tests, addresses Gemini/Claude feedback with fix-only commits, commits, pushes, and opens PRs. Codex must not use `OPENAI_API_KEY`. |
| Gemini Flash | Reviews `feature/ticket-N` to `pre-dev` PRs for specification compliance against requirements, API, ERD, SRS, and ticket scope. |
| Claude Sonnet | Reviews `pre-dev` to `dev` PRs for code quality, architecture, security, transactions, tests, and maintainability. It uses `CLAUDE_CODE_OAUTH_TOKEN`, must not use `ANTHROPIC_API_KEY`, runs with overflow off, and is review-only. |
| GitHub Actions | Runs CI, manages labels, enforces review round trigger conditions, and enforces merge gates through check runs. |
| Human | Makes early `dev` merge decisions, decides after round-2 failures, and controls `main` releases. |

## 2. Branch Flow

```text
feature/ticket-N -> pre-dev -> dev -> main
```

Gate selection is based on the PR base branch:

- `base=pre-dev`: first gate, Gemini specification review.
- `base=dev`: second gate, Claude CTO review.

The primary source for identifying the ticket is the `Ticket ID` field in the PR body.

### Derived Policies

- Ad-hoc PRs without a ticket, including `fix/*` or `docs/*`, do not receive automatic approval. They must be labeled `needs-human-review`.
- Tickets that depend on an earlier ticket may branch from `pre-dev`. The default branch point is `dev`.
- Docs-only PRs in the first gate do not call Gemini. If CI passes, the specification review check should finish with success immediately.

## 3. Daily Development Flow

Codex Builder:

1. Reads `docs/README.md`, `docs/AI_RULES.md`, and `docs/tickets.md`.
2. Reads only the documents relevant to the target ticket.
3. Creates or works on `feature/ticket-N`.
4. Implements the ticket and required tests.
5. Opens a PR from `feature/ticket-N` to `pre-dev`.

## 4. First Gate: Specification Review

For `feature/ticket-N -> pre-dev` PRs:

- CI runs build/test/lint.
- Gemini Flash checks the PR against ticket scope, API, ERD, SRS, backend design, ownership rules, validation rules, and required tests.

Results:

- CI success and Gemini `[APPROVE]`: GitHub Actions merges into `pre-dev`.
- CI failure or Gemini `[REJECT]`: Codex fixes only blocking issues and pushes to the same branch for revalidation.
- Gemini Action failure, authentication failure, quota issue, or infrastructure failure is not treated as `[REJECT]`. Attach `needs-human-review` and distinguish the failure in the PR comment. The optional `gemini-unavailable` label is an open decision; initially, do not create it and use comments to distinguish the failure.

## 5. Second PR Creation

When `pre-dev` is updated, GitHub Actions creates or updates a PR from `pre-dev` to `dev`.

The PR body must maintain an AI review log source table with:

| Ticket PR | Ticket ID | Gemini Decision |
|---|---|---|
|  |  |  |

Claude uses this table when writing the final conflict log.

## 6. Docs-only Policy

Docs-only files:

- `docs/**`
- `*.md`
- `.github/ai/**`

Files treated as code or operational changes:

- `.github/workflows/**`
- `build.gradle`
- `settings.gradle`
- `gradle/**`
- `src/**`
- `docker/**`
- `Dockerfile`
- `docker-compose.yml`
- `application*.yml`

For docs-only `pre-dev -> dev` PRs:

- The `claude-cto-review` check must still run.
- It exits successfully immediately instead of skipping the check.
- Attach the `docs-only-review-passed` label.
- Do not attach `merge-ready` solely because the PR is docs-only.

## 7. Second Gate: CTO Code Review

For `pre-dev -> dev` PRs that include code or operational changes:

- CI runs build/test/lint.
- Claude Sonnet runs CTO review.
- Claude is review-only and must not modify code, commit, or push.
- Claude uses `CLAUDE_CODE_OAUTH_TOKEN`.
- `ANTHROPIC_API_KEY` is forbidden.
- Overflow is off.

## 8. Round Trigger Rules

Round 1 runs when:

- The `pre-dev -> dev` PR is first created.
- The PR is updated with code changes.

Round 2 runs only when one of these conditions is true:

1. A Codex fix commit message contains `[claude-followup]`.
2. A human manually attaches the `claude-review` label.

For a normal push without the required tag:

- Remove `merge-ready`.
- Do not rerun Claude automatically.
- Attach `needs-re-review`.

## 9. Ping-pong Limit

Round 1:

- Full CTO review.
- Maximum 5 Blocking Issues.
- If there are more than 5, list only the 5 most severe blockers and move the rest to recommendations.
- Must define Follow-up Scope.

Codex Fix:

- Fix only Blocking Issues.
- Do not implement new features.
- Do not perform unrelated refactoring.
- Include `[claude-followup]` in the fix commit message.

Round 2:

- Check only whether the previous Blocking Issues are resolved.
- Do not re-review the full diff.
- New blockers are allowed only when the Codex fix itself introduces a serious correctness, security, data consistency, test, or operational risk.

After Round 2, if Claude still returns `[CHANGES_REQUESTED]`:

- Stop automatic Claude reruns.
- Attach `needs-human-review`.
- Human decides the next step.

## 10. Claude Result Handling

`[MERGE_READY]`:

- Attach `merge-ready`.
- Remove `needs-re-review`.
- Initially, a human performs the `dev` merge. Auto-merge may be considered after stabilization.

`[CHANGES_REQUESTED]`:

- Attach `needs-codex-fix`.
- Remove `merge-ready`.
- Codex fixes only Blocking Issues.
- Recommended Improvements are not implemented in the fix pass unless a human explicitly requests them.
- Codex pushes a fix commit containing `[claude-followup]`.

Claude Action failure, authentication failure, credit exhaustion, OAuth issue, or Action infrastructure failure:

- Attach `claude-unavailable`.
- Attach `needs-human-review`.
- Do not send the PR into the Codex fix flow.

## 11. Label Policy

| Label | Meaning | Managed by |
|---|---|---|
| `claude-review` | Human manually triggers Claude review. | Human |
| `claude-round-1` | Claude round 1 review completed. | Workflow |
| `claude-round-2` | Claude round 2 follow-up review completed. | Workflow |
| `needs-codex-fix` | Blockers exist and Codex must fix them. | Workflow |
| `needs-re-review` | Code changed but review trigger conditions were not met. | Workflow |
| `needs-human-review` | Automatic loop stopped or reviewer call failed; human decision required. | Workflow |
| `claude-unavailable` | Claude Action itself failed. | Workflow |
| `merge-ready` | Claude review conditions passed. | Workflow |
| `docs-only-review-passed` | Docs-only change passed Claude review check immediately. | Workflow |

## 12. Merge Gate

Labels are for visibility only.

Actual merge eligibility is controlled by required status checks:

- `ci`
- `claude-cto-review`

The branch protection rule for `dev` must require both checks to be successful.

`claude-cto-review` succeeds when:

- Claude returns `[MERGE_READY]`.
- The PR is docs-only and the workflow exits successfully immediately.

## 13. AI Review Conflict Log

The final Claude PR comment must include this table:

| 시점 | 모델 | 판정 | 근거 요약 |
|---|---|---|---|
| 1차 | Gemini Flash | APPROVE/REJECT |  |
| N차 | Claude Sonnet | MERGE_READY/CHANGES_REQUESTED |  |

Conflict rules:

- A difference between result validation by Gemini and process/code validation by Claude is not a conflict.
- Only directly contradictory conclusions on the same item are conflicts.
- Real conflicts attach `needs-human-review`.

## 14. Secrets Inventory

Required secrets:

- `GEMINI_API_KEY`
- `CLAUDE_CODE_OAUTH_TOKEN`

Forbidden secrets:

- `ANTHROPIC_API_KEY`
- `OPENAI_API_KEY`

Do not expose secret values in logs, comments, prompts, or documentation.

## 15. Release

`dev -> main` is not automated.

The human maintainer decides release timing.

Claude Fable / Opus may be called before `main` when a high-difficulty audit is needed.
