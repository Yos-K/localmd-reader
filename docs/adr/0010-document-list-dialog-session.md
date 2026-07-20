# ADR-0010: Model Document-List Dialogs as One Exclusive Session

Status: Accepted

## Decision

Represent the recent-files, pinned-files, and folder-files dialogs as mutually
exclusive variants of one `DocumentListDialogState`. Convert item selection and
the secondary action into explicit `DocumentListCommand` variants. A dedicated
`DocumentListDialogController` owns the active session and adapts commands to
Android effects; `MainActivity` only delegates requests to that controller.

## Alternatives Considered

- Keep one boolean and one data field for each dialog kind in `MainActivity`.
- Infer the list kind from the current dialog title or button label.
- Give each list kind a separate Android listener without a shared model.
- Use one exclusive session model and one Android adapter.

## Why This Decision

Independent booleans allowed contradictory states and forced a shared Android
callback to reconstruct user intent from presentation fields. Explicit variants
make the source list and every valid outcome unambiguous, while total selection
turns stale or out-of-range callbacks into a modeled no-op. Moving Android
dialog orchestration out of `MainActivity` also reduces its responsibilities.

## Why Alternatives Were Rejected

More booleans preserve invalid combinations. Titles and labels are localized
rendering values and cannot be authoritative state. Separate listeners remove
some branching but duplicate selection rules and do not provide one model that
the interaction harness and tests can verify.

## Reconsider When

Reconsider the dialog presentation when document lists move to a persistent
screen or panel, when list-specific commands no longer share a selection
contract, or when an Android navigation component can own the session with
equivalent always-valid and testable guarantees.
