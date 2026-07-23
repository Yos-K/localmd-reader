# ADR-0019: Keep Pinned Documents Independent from Open Tabs

Status: Accepted

## Decision

Model a pinned document as a persistent bookmark to a saved Markdown file, not
as a property owned by an open tab. A Pro file tab may toggle that bookmark by
long press and must show a pin icon and localized accessibility description
while its document is pinned. Menu actions and long press use the same pin and
unpin behavior, and every mutation refreshes tab marks and menu actions.

Closing a tab does not unpin its document. The pinned-files list offers opening
the selected file, unpinning only that file, and explicitly clearing all pins.
Welcome and temporary draft tabs cannot enter the pinned-document model.

## Alternatives Considered

- Treat pinning as an open-tab property that disappears when the tab closes.
- Permit pinning only from menu actions.
- Keep only a clear-all operation in the pinned-files list.
- Model persistent bookmarks independently and project their state onto tabs.

## Why This Decision

Users pin a file so they can return after closing it or restarting the app. A
bookmark therefore outlives a tab. Long press provides a direct operation at
the object being pinned, while the icon makes the resulting state visible.
Shared mutation behavior prevents the tab icon and menu actions from diverging.
Individual unpinning avoids destroying unrelated bookmarks.

## Why Alternatives Were Rejected

Tab-owned pinning loses the main retrieval benefit when a tab closes. Menu-only
pinning hides a frequent operation away from the tab. Clear-all-only removal
forces users to discard valid bookmarks to remove one unwanted file. Separate
mutation paths previously left stale pin icons after menu operations.

## Reconsider When

Reconsider if Android supplies a more accessible standard bookmark interaction,
if long press conflicts with essential tab accessibility gestures, or if pinned
documents become a synchronized collection with identity beyond local URIs.
