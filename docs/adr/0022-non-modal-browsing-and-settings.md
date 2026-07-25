# ADR-0022: Keep Browsing, Selection, and Settings Non-Modal

Status: Accepted

## Decision

Present browsing and selection features, including recent files, pinned files,
the table of contents, the Markdown library, and gestures, as expandable panels
inside the navigation menu. Prefer non-modal controls for themes and display
settings so choices can be compared without losing context.

Place every expandable panel immediately after its owning action row and inside
the same visual group. Do not collect panels at the end of a card. Communicate
ownership through the row chevron, immediate placement, and left indentation.

Reserve dialogs for actions that require an explicit response before work can
continue: naming a saved file, purchasing, confirming destructive operations,
or reporting an error that prevents continuation.

## Alternatives Considered

- Continue presenting lists and settings with Android `AlertDialog`.
- Replace standard dialogs with custom-styled dialogs.
- Standardize on expandable panels inside the navigation menu.
- Add a separate screen for every list.

## Why This Decision

Expandable panels preserve the current document and navigation context, and let
people compare choices or repeat actions without reopening a modal surface. The
table of contents and Markdown library already establish this interaction model.
Reusing it keeps theme, language, and accessibility behavior consistent. Limiting
dialogs to operations that truly require a response also restores clear modal
semantics.

## Why Alternatives Were Rejected

Standard dialogs are visually inconsistent with app themes and interrupt context.
Custom dialogs improve appearance but retain the modal and repeated-navigation
problems. Separate screens add excessive navigation depth for simple lists and
settings.

## Reconsider When

Reconsider if menu content becomes too large for accessible navigation, a list
requires complex editing or search, or Android large-screen guidance requires a
dedicated navigation pane.
