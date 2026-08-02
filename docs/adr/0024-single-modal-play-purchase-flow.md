# ADR-0024: Keep the Google Play Sheet as the Only Purchase Modal

Status: Accepted

## Decision

When a Pro purchase starts successfully, show the Google Play purchase sheet as
the only modal surface. Keep `inProgress` in the purchase UI model, but do not
open an application `AlertDialog` for that state. Show an application message
dialog only when purchase startup resolves to a state that requires an
explanation, such as unavailable or pending.

## Alternatives Considered

- Show an application "Opening purchase" dialog behind the Google Play sheet.
- Keep the application dialog and explicitly dismiss it from every Billing
  completion callback.
- Use only the Google Play sheet while startup is progressing.

## Why This Decision

The Google Play sheet already communicates that the purchase flow is active.
Using it as the single modal surface prevents a stale application dialog from
remaining after cancellation and keeps ownership of purchase cancellation at
the Billing boundary. The domain result still controls whether a later failure
needs an explanation.

## Why Alternatives Were Rejected

Two stacked modal surfaces obscure which component owns completion. Explicit
dismissal couples every Billing callback to one presentation object and remains
fragile when Android closes or recreates either surface. A second progress
dialog provides no information that the Google Play sheet does not already show.

## Reconsider When

Reconsider if Google Play no longer provides a visible purchase surface, Android
adds a lifecycle callback that reliably owns the complete sheet lifetime, or
accessibility testing shows that an additional non-modal progress announcement
is required.
