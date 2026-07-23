# Architecture Decision Records

ADRs preserve decisions that have multiple plausible implementations and would
be costly to rediscover from code or pull-request history.

## Rules

- Canonical ADRs are written in English; each has a `.ja.md` companion.
- ADRs are immutable records of a decision. Change status or add a superseding
  ADR instead of silently rewriting history when the decision changes.
- Every `feat:` and `fix:` commit must include one `ADR-Review:` trailer.
- A structural change prompted by user feedback must create, update, or
  supersede an ADR before implementation is accepted.

Required sections are Decision, Alternatives Considered, Why This Decision,
Why Alternatives Were Rejected, and Reconsider When.

## Index

| ADR | Decision | Status |
|---|---|---|
| [0001](0001-offline-security-boundary.md) | Offline security boundary | Accepted |
| [0002](0002-free-pro-product-boundary.md) | Free/Pro product boundary | Accepted |
| [0003](0003-markdown-library-navigation.md) | Persistent menu-tree Markdown library | Accepted |
| [0004](0004-android-storage-access-framework.md) | Android Storage Access Framework | Accepted |
| [0005](0005-isolated-mermaid-rendering.md) | Isolated local Mermaid rendering | Accepted |
| [0006](0006-gradle-ci-build-authority.md) | Gradle CI build authority | Accepted |
| [0007](0007-blocking-adr-review-gate.md) | Blocking ADR review gate | Accepted |
| [0008](0008-interaction-command-traceability.md) | Model-code-test interaction traceability | Accepted |
| [0009](0009-interaction-state-above-views.md) | Interaction state above Android views | Accepted |
| [0010](0010-document-list-dialog-session.md) | Document-list dialog session ownership | Accepted |
| [0011](0011-inline-menu-disclosure-state.md) | Inline menu disclosure state | Accepted |
| [0012](0012-derived-heading-navigation.md) | Derived heading navigation without sentinel state | Accepted |
| [0013](0013-document-tab-session-completion.md) | Document tab session completion | Accepted |
| [0014](0014-public-source-private-release-repositories.md) | Public source and private release repositories | Accepted |
| [0015](0015-document-rendering-coordinator.md) | Platform-independent rendering session ownership | Accepted |
| [0016](0016-open-document-tab-session-owner.md) | Platform-independent open document tab ownership | Accepted |
| [0017](0017-free-file-opening-and-pro-library-boundary.md) | One Free file entry point and persistent Pro library | Accepted |
| [0018](0018-language-complete-view-composition.md) | Complete language-specific view composition | Accepted |
| [0019](0019-pinned-document-bookmark-semantics.md) | Pinned documents independent from open tabs | Accepted |
