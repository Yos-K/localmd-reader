# ADR-0012: Derive Heading Navigation Without Sentinel State

Status: Accepted

## Decision

Represent a heading-navigation result with the always-valid
`HeadingNavigation` variants `Unavailable` and `Destination`. Derive the
current destination from the active document's table of contents and current
scroll position whenever a next or previous command is invoked. Do not retain a
separate mutable heading index in `MainActivity`.

## Alternatives Considered

- Keep `activeHeadingShortcutIndex` with `-1` meaning no active heading.
- Store an optional current heading and reset it whenever the document changes.
- Infer the destination directly in each Android gesture callback.
- Derive one typed navigation value from document headings and scroll position.

## Why This Decision

The visible WebView position is already authoritative for what the user is
reading. A second mutable index can become stale after scrolling, opening a
document, or switching tabs. `Unavailable` makes a heading-free document and an
unrelated selected heading explicit, while `Destination` can guarantee that
next and previous always produce an in-range destination and wrap at both ends.

## Why Alternatives Were Rejected

A `-1` sentinel permits invalid arithmetic and spreads boundary checks through
`MainActivity`. A stored optional heading still requires synchronization with
scrolling and document lifecycle events. Gesture-specific inference duplicates
the same navigation rules and prevents reuse by the table of contents.

## Reconsider When

Reconsider when heading visibility can be reported exactly by the renderer,
when navigation must preserve a user-selected heading independently of scroll
position, or when navigation history becomes a product requirement.
