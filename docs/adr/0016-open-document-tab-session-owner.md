# ADR-0016: Own Open Document Tabs in a Platform-Independent Session

Status: Accepted

## Decision

Let `OpenDocumentTabSession` be the single mutable owner of
`OpenDocumentTabs`. Document opening, activation, navigation, closing, and
rendered-document replacement issue commands to that session. Android
presentation code may read the current immutable `OpenDocumentTabs` snapshot,
but it must not store or replace the snapshot directly.

Keep Android completion effects such as refreshing controls, rendering the
active document, and persistence in `DocumentTabSessionController` or the
relevant presentation adapter. The session reports replacement through a small
handler so Android effects remain outside the platform-independent owner.

## Alternatives Considered

- Keep `OpenDocumentTabs` as a mutable field on `MainActivity`.
- Let each opening and navigation entry point replace an immutable snapshot.
- Combine tab and rendering ownership into one workspace aggregate immediately.
- Introduce a platform-independent owner dedicated to open-document tabs.

## Why This Decision

Tabs are changed by file opening, external intents, clipboard drafts, tab
controls, gestures, saving, and asynchronous rendering completion. Direct
snapshot assignment made correctness depend on every entry point remembering
the same ownership rules. A single owner preserves the always-valid tab set and
makes every state change explicit while retaining immutable snapshots for reads.

Keeping tab and rendering owners separate also permits the migration away from
`MainActivity` to proceed in small verified steps. Their coordinated close
behavior remains represented by `DocumentTabCloseResult`.

## Why Alternatives Were Rejected

A `MainActivity` field keeps Android as the domain-state owner. Independent
snapshot replacement permits call sites to bypass shared behavior and creates
multiple effective owners. Combining tabs and rendering now would broaden this
refactoring before their aggregate boundary and lifecycle requirements are
established.

## Reconsider When

Reconsider if multiple windows must share one document workspace, if open tabs
must outlive the Activity in a process-level service, or if evidence shows that
tab and rendering sessions must become one transactional aggregate.
