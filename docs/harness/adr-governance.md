# ADR Governance

## Goal

Prevent feature work from silently reversing decisions that were previously
made from user feedback, security constraints, or build-system evidence.

## Workflow

1. Read `docs/adr/README.md` before feature or behavior work.
2. Identify applicable decisions and reconsideration triggers.
3. If feedback changes structure, create, update, or supersede the ADR first.
4. Implement with tests and synchronized rule documentation.
5. Add exactly one `ADR-Review:` trailer to every `feat:` or `fix:` commit.
6. Run `sh scripts/check-adr-review.sh` through `pr-preflight` before opening a PR.

## Gate

`check-adr-review.sh` examines every feature and fix commit between the base and
HEAD. A commit must reference one or more canonical ADR paths, or use a reasoned
`none`. Referenced ADRs must exist at that commit and have a Japanese companion.
Every ADR must contain the five required decision sections.

The guard runs in the required CI `fitness` job. A missing declaration therefore
blocks merge rather than producing an advisory warning.

## Limits

The guard proves that review was declared and that decision records are
well-formed. It cannot prove that a `none` reason is honest or that an
implementation semantically obeys the ADR. Code review, interaction models, and
tests still verify those claims. Repeated misuse of `none` is a trigger to narrow
the allowed form or require an ADR path for the affected change category.

## Historical Audit

The initial audit covered current `main` and the pre-rewrite backup history.
It recovered six decisions: offline security, Free/Pro boundaries, persistent
Markdown library navigation, SAF storage access, isolated Mermaid rendering,
and Gradle CI build authority. Pricing, copy, and isolated visual adjustments
remain in product policy or normal specifications because they are expected to
change without architectural migration.

