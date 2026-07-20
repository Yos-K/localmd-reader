# ADR-0011: Model Inline Menu Sections as Disclosure State

Status: Accepted

## Decision

Represent the settings and table-of-contents sections in the navigation menu
with the shared always-valid `DisclosureState` variants `Collapsed` and
`Expanded`. Let `ExpandableMenuSection` own the state-to-visibility mapping and
expanded-content refresh. Remove the unreachable hierarchical settings-dialog
implementation and model the actual inline settings interaction instead.
`TableOfContentsMenuPanel` owns construction and restyling of table-of-contents
rows; `MainActivity` supplies the current headings and handles a selected row.

## Alternatives Considered

- Keep one presentation boolean for each expandable section.
- Infer expansion from Android view visibility.
- Restore the unused hierarchical settings dialogs to match the old model.
- Share one disclosure behavior while keeping settings and contents as distinct
  interaction states in the harness.

## Why This Decision

Both sections have the same total transition: every toggle changes one valid
collapsed or expanded state into the other. A shared type prevents visibility
from becoming authoritative state, while distinct harness states preserve their
different commands and content. Removing the unreachable dialogs aligns the
model, implementation, and user-visible workflow and reduces dead code.

## Why Alternatives Were Rejected

Independent booleans and visibility inference repeat the failure addressed by
ADR-0009. Restoring dialogs would make settings slower and contradict the
accepted inline UX. Treating settings and contents as one harness surface would
erase their different completion and recovery commands. Keeping row rendering
in `MainActivity` would leave the modeled section split across unrelated widget
construction and styling methods.

## Reconsider When

Reconsider when a section needs more than collapsed and expanded states, when
multiple sections require mutual exclusion, or when settings move to a dedicated
screen whose navigation lifecycle supersedes inline disclosure.
