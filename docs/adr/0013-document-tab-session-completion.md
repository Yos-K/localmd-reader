# ADR-0013: Complete Document Tab Session Transitions in One Controller

Status: Accepted

## Decision

Keep `OpenDocumentTabs` as the always-valid tab state and route tab activation,
closing, previous, and next commands through `DocumentTabSessionController`.
The controller completes each transition by updating the authoritative state,
clearing stale status, refreshing localized controls and tab views, rendering
the active document, and persisting restorable tabs.

Opening a new document remains with its source-specific coordinator because
file opening, external intents, and clipboard drafts have different validation,
history, persistence, and anchor requirements.

## Alternatives Considered

- Keep the completion sequence duplicated in each click and gesture handler.
- Move Android rendering and persistence into `OpenDocumentTabs`.
- Route every operation that creates, replaces, selects, or closes a tab through
  one controller immediately.
- Centralize only the equivalent selection and closing transitions first.

## Why This Decision

Click and gesture entry points previously performed the same multi-step update.
Omitting any step could leave the visible document, tab strip, status message,
or restored session inconsistent with `OpenDocumentTabs`. One controller makes
completion atomic at the application boundary without adding Android concerns
to the immutable viewer model.

## Why Alternatives Were Rejected

Duplicated orchestration permits entry-point-specific regressions. Putting
Android views or persistence in `OpenDocumentTabs` reverses the dependency
direction. Moving all open flows at once would hide meaningful differences such
as recent-file recording, temporary-document persistence, and target anchors.

## Reconsider When

Reconsider when document-opening policies are represented as explicit commands
or effects, when tab state moves out of `MainActivity` entirely, or when a
background/session service becomes the authoritative owner of open documents.
