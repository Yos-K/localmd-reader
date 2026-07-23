# ADR-0017: Use One File Entry Point in Free and a Persistent Library in Pro

Status: Accepted

## Decision

Free exposes Android's ordinary file picker as its only local-file selection
entry point. It does not expose a separate one-shot "Choose from folder"
action. Pro exposes the Markdown Library, whose selected root and expandable
menu tree persist across document selections as defined by ADR-0003.

## Alternatives Considered

- Keep both ordinary file opening and one-shot folder selection in Free.
- Give Free the same persistent Markdown Library as Pro.
- Remove folder-based navigation from both editions.
- Keep one file entry point in Free and reserve persistent library navigation
  for Pro.

## Why This Decision

The one-shot Free flow repeats ordinary file selection with an extra folder
step and does not retain navigation context. The Pro library has distinct value:
it keeps a project root and tree available while users switch among related
documents. This preserves a complete reader in Free while Pro improves repeated
navigation rather than repairing intentional friction.

## Why Alternatives Were Rejected

Two transient Free entry points make the same task harder to understand. Giving
Free the persistent library would erase the established convenience boundary.
Removing the library entirely would discard a useful workflow for document
sets. The selected boundary keeps each entry point behaviorally distinct.

## Reconsider When

Reconsider if Android's file picker no longer provides usable single-file
selection, evidence shows that a persistent library is essential to basic
reading, or the Free/Pro product model changes substantially.
