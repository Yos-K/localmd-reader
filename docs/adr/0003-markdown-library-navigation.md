# ADR-0003: Keep the Markdown Library as a Persistent Menu Tree

Status: Accepted

## Decision

The Pro Markdown library is a collapsible tree in the scrollable hamburger
menu, separate from the ordinary file actions. Its action row uses the same
expand/collapse chevron as the table of contents. Tapping that row collapses or
reopens a loaded tree without discarding its selected root, current location,
or filter. Before a tree has been loaded, the same action opens Android's folder
picker. Opening a document may close the menu for reading, but reopening the
menu preserves the tree state so another project file remains close at hand.

## Alternatives Considered

- Reopen Android's picker for every document.
- Show the library as a one-shot selection dialog.
- Use a dedicated full-screen project browser.
- Keep a persistent tree inside the existing menu.

## Why This Decision

The library's value is repeated navigation across related files, not another
way to select one file. The menu already acts as the reader's navigation surface
and is reachable by button or edge swipe without replacing the document view.

## Why Alternatives Were Rejected

Repeated pickers lose project context. A dialog ends the flow after one file and
duplicates ordinary file selection. A full-screen browser adds another top-level
mode before the library requires that scale.

## Reconsider When

Reconsider if project operations outgrow the menu, accessibility testing shows
the tree cannot be operated reliably there, or a full-screen library can prove
fewer steps without obscuring the active document workflow.
