# ADR-0007: Block Feature and Fix Commits without ADR Review

Status: Accepted

## Decision

Keep architectural decisions in bilingual ADRs and require exactly one
`ADR-Review:` trailer on every `feat:` and `fix:` commit. Validate declarations,
references, companion documents, and required ADR sections in local hooks,
preflight, and the required CI fitness gate.

## Alternatives Considered

- Keep decisions only in pull-request descriptions.
- Document ADR guidance without automation.
- Run an advisory ADR report that does not block merge.
- Enforce review declarations per feature and fix commit.

## Why This Decision

Repository rewrites and later implementation work can hide earlier UX and
architecture context. A commit-level declaration makes review contemporaneous
with the change, while a required gate prevents silent omission by any agent or
human workflow.

## Why Alternatives Were Rejected

PR descriptions are transient implementation context and may be unavailable or
overlooked later. Guidance without enforcement already allowed a persistent menu
tree decision to be reversed. Advisory checks do not close the acceptance path.

## Reconsider When

Reconsider if the gate creates measured false-positive cost greater than the
regressions it prevents, if commits cease to be the review unit, or if a stronger
semantic decision-impact system can replace trailers without weakening auditability.

