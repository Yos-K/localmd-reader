# ADR-0015: Own Rendering State in a Platform-Independent Coordinator

Status: Accepted

## Decision

Let `DocumentRenderingCoordinator` be the single owner of
`DocumentRenderingSession`. Route document opening, Mermaid completion, theme
refresh, and tab closing through that coordinator. Expose rendering, background
job submission, and visible-tab refresh through a small `Output` port implemented
by the Android presentation adapter.

`MainActivity` may issue commands and provide current rendering configuration,
but it must not store or replace the rendering session directly.

## Alternatives Considered

- Keep the session and all result-application steps in `MainActivity`.
- Move the same procedures into an Android-dependent controller.
- Let every caller receive the next session and apply its effects independently.
- Use a platform-independent coordinator with an output port.

## Why This Decision

Rendering transitions always require the next session to become authoritative
before their jobs or screen updates are applied. A pure coordinator makes that
ordering one tested behavior and prevents entry points from omitting one effect.
The output port keeps Android, WebView, and the concrete renderer outside the
state owner, so the orchestration is covered by fast JVM tests.

## Why Alternatives Were Rejected

Keeping the procedure in `MainActivity` preserves duplicated mutable ownership.
An Android controller only relocates the same coupling and requires Robolectric
to verify sequencing. Returning sessions to every caller permits partial
application and makes correctness depend on call-site discipline.

## Reconsider When

Reconsider if rendering moves to a process-level service, if multiple windows
must share one rendering session, or if background work survives the Activity
lifecycle and therefore needs a persistent session owner.
